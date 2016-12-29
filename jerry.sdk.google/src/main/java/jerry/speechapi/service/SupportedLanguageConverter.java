package jerry.speechapi.service;

import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import jerry.speechapi.client.lang.SupportedLanguage;
import okhttp3.ResponseBody;
import retrofit2.Converter;

class SupportedLanguageConverter implements Converter<ResponseBody, List<SupportedLanguage>> {
    private static final Pattern PATTERN_HYPHEN = Pattern.compile("-");

    @NonNull
    @Override
    public List<SupportedLanguage> convert(@NonNull ResponseBody value) throws IOException {
        try {
            String html = value.string();
            String tbodyXml = htmlToTbodyXml(html);
            XmlPullParser xmlPullParser = createXmlPullParser(tbodyXml);
            return parseTbodyXml(xmlPullParser);
        } catch (Throwable throwable) {
            if (throwable instanceof IOException) {
                throw (IOException) throwable;
            } else {
                throw new IOException("Error during conversion from ResponseBody to a supported language list.", throwable);
            }
        }
    }

    @NonNull
    private static String htmlToTbodyXml(@NonNull String html) throws Throwable {
        String tbodyTag = extractTbodyTag(html);
        if (TextUtils.isEmpty(tbodyTag)) {
            throw new IllegalArgumentException("<tbody> tag couldn't be found. - " + html);
        }
        return convertToXmlDocument(tbodyTag);
    }

    private static String extractTbodyTag(@NonNull String html) {
        int startTbody = html.indexOf("<tbody>");
        if (startTbody < 0) {
            return null;
        }
        int endTbody = html.indexOf("</tbody>", startTbody);
        if (endTbody < 0) {
            return null;
        }
        return html.substring(startTbody, endTbody + "</tbody>".length());
    }

    private static String convertToXmlDocument(@NonNull String tbodyTag) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + tbodyTag;
    }

    @NonNull
    private static XmlPullParser createXmlPullParser(@NonNull String tbodyXml) throws Throwable {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser parser = factory.newPullParser();
        parser.setInput(new StringReader(tbodyXml));
        return parser;
    }

    private static List<SupportedLanguage> parseTbodyXml(@NonNull XmlPullParser parser) throws Throwable {
        List<SupportedLanguage> tableRows = new LinkedList<>();
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG: {
                    if ("tr".equalsIgnoreCase(parser.getName())) {
                        List<String> tableRow = parseTableRow(parser);
                        SupportedLanguage convertedEntry = convertElementsToSupportedLanguage(tableRow);
                        tableRows.add(convertedEntry);
                    }
                    break;
                }
            }
            eventType = parser.next();
        }
        return tableRows;
    }

    @NonNull
    private static List<String> parseTableRow(
            @NonNull XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        List<String> elements = null;
        boolean addNextText = false;
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_TAG: {
                    if ("tr".equalsIgnoreCase(parser.getName())) {
                        elements = new ArrayList<>(3);
                    } else if ("td".equalsIgnoreCase(parser.getName())) {
                        addNextText = true;
                    }
                    break;
                }
                case XmlPullParser.END_TAG: {
                    if ("tr".equalsIgnoreCase(parser.getName())) {
                        if (elements == null || elements.size() != 3) {
                            throw new XmlPullParserException("Invalid format. - elements: " + elements);
                        }
                        return elements;
                    }
                    break;
                }
                case XmlPullParser.TEXT: {
                    if (addNextText) {
                        if (elements != null) {
                            elements.add(parser.getText().trim());
                        }
                        addNextText = false;
                    }
                    break;
                }
            }
            eventType = parser.next();
        }

        // It cannot be reached here.
        throw new XmlPullParserException("Invalid format.");
    }

    private static SupportedLanguage convertElementsToSupportedLanguage(@NonNull List<String> elements) {
        String languageName = elements.get(0);
        String languageCode = elements.get(1);
        String languageNameInEnglish = elements.get(2);
        return new SupportedLanguage(
                languageCode,
                convertLanguageCodeToLocale(languageCode),
                languageName,
                languageNameInEnglish
        );
    }

    private static Locale convertLanguageCodeToLocale(@NonNull String languageCode) {
        String[] languageCodeElement = PATTERN_HYPHEN.split(languageCode);
        String language, region, script;
        switch (languageCodeElement.length) {
            case 1: {
                language = androidCompatibleLanguage(languageCodeElement[0]);
                region = script = null;
                break;
            }
            case 2: {
                language = androidCompatibleLanguage(languageCodeElement[0]);
                region = languageCodeElement[1];
                script = null;
                break;
            }
            case 3: {
                language = androidCompatibleLanguage(languageCodeElement[0]);
                region = languageCodeElement[2];
                script = languageCodeElement[1];
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid format: " + languageCode);
            }
        }
        return createLocale(language, region, script);
    }

    private static Locale createLocale(@NonNull String language, @Nullable String region, @Nullable String script) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.Builder localeBuilder = new Locale.Builder();
            localeBuilder.setLanguage(language);
            if (region != null) {
                localeBuilder.setRegion(region);
            }
            if (script != null) {
                localeBuilder.setScript(script);
            }
            return localeBuilder.build();
        } else {
            if (region == null) {
                return new Locale(language);
            } else if (script == null) {
                return new Locale(language, region);
            } else {
                return new Locale(language, region, script);
            }
        }
    }

    private static String androidCompatibleLanguage(String language) {
        // Chinese
        if ("cmn".equalsIgnoreCase(language) || "yue".equalsIgnoreCase(language)) {
            return "zh";
        }
        // Filipino
        if ("fil".equalsIgnoreCase(language)) {
            return "tl";
        }
        return language;
    }
}
