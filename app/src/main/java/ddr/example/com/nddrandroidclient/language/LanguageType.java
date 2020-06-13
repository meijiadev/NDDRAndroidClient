package ddr.example.com.nddrandroidclient.language;

/**
 * desc：语言种类
 * time：2020/06/12
 */
public enum LanguageType {
    CHINESE("ch"),
    ENGLISH("en");

    private String language;

    LanguageType(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language == null ? "" : language;
    }
}
