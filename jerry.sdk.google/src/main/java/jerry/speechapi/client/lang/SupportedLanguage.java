package jerry.speechapi.client.lang;

import android.support.annotation.NonNull;

import java.util.Locale;

public final class SupportedLanguage implements Comparable<SupportedLanguage> {
    @NonNull
    private final String code;

    @NonNull
    private final Locale locale;

    @NonNull
    private final String name;

    @NonNull
    private final String nameInEnglish;

    public SupportedLanguage(
            @NonNull String code,
            @NonNull Locale locale,
            @NonNull String name,
            @NonNull String nameInEnglish) {
        this.code = code;
        this.locale = locale;
        this.name = name;
        this.nameInEnglish = nameInEnglish;
    }

    @NonNull
    public String getCode() {
        return code;
    }

    @NonNull
    public Locale getLocale() {
        return locale;
    }

    @NonNull
    public String getName() {
        return name;
    }

    @NonNull
    public String getNameInEnglish() {
        return nameInEnglish;
    }

    @NonNull
    public String getDisplayName() {
        return locale.getDisplayName();
    }

    @Override
    public String toString() {
        return "SupportedLanguage{" +
                "code='" + getCode() + '\'' +
                ", locale=" + getLocale() +
                ", name='" + getName() + '\'' +
                ", nameInEnglish='" + getNameInEnglish() + '\'' +
                ", displayName='" + getDisplayName() + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull SupportedLanguage o) {
        return getName().compareTo(o.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SupportedLanguage that = (SupportedLanguage) o;

        return code.equals(that.code);

    }

    @Override
    public int hashCode() {
        return code.hashCode();
    }
}
