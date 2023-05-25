package org.nofs.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.nofs.config.Configuration;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/* loaded from: org.nofs.jar:emu/org.nofs/utils/Language.class */
public final class Language {
    private static final Map<String, Language> cachedLanguages = new ConcurrentHashMap();
    private final JsonObject languageData;
    private final String languageCode;
    private final Map<String, String> cachedTranslations = new ConcurrentHashMap();
    private static final int TEXTMAP_CACHE_VERSION = -1664430589;

    public static Language getLanguage(String langCode) {
        Language languageInst;
        if (cachedLanguages.containsKey(langCode)) {
            return cachedLanguages.get(langCode);
        }
        String fallbackLanguageCode = Utils.getLanguageCode(Configuration.FALLBACK_LANGUAGE);
        LanguageStreamDescription description = getLanguageFileDescription(langCode, fallbackLanguageCode);
        String actualLanguageCode = description.getLanguageCode();
        if (description.getLanguageFile() != null) {
            languageInst = new Language(description);
            cachedLanguages.put(actualLanguageCode, languageInst);
        } else {
            languageInst = cachedLanguages.get(actualLanguageCode);
            cachedLanguages.put(langCode, languageInst);
        }
        return languageInst;
    }

    public static String translate(String key, Object... args) {
        String obj;
        String translated = org.nofs.sdkserver.getLanguage().get(key);
        for (int i = 0; i < args.length; i++) {
            int i2 = i;
            String simpleName = args[i].getClass().getSimpleName();
            boolean z = true;
            switch (simpleName.hashCode()) {
                case -1808118735:
                    if (simpleName.equals("String")) {
                        z = false;
                        break;
                    }
                    break;
                case -1053826955:
                    if (simpleName.equals("TextStrings")) {
                        z = true;
                        break;
                    }
                    break;
            }
            if (!(z)) {
                obj = (String) args[i];
            } else if (z) {
                obj = ((TextStrings) args[i]).get(0).replace("\\\\n", "\\n");
            } else {
                obj = args[i].toString();
            }
            args[i2] = obj;
        }
        try {
            return translated.formatted(args);
        } catch (Exception exception) {
            org.nofs.sdkserver.getLogger().error("Failed to format string: " + key, (Throwable) exception);
            return translated;
        }
    }

    public String getLanguageCode() {
        return this.languageCode;
    }

    private Language(LanguageStreamDescription description) {
        JsonObject languageData = null;
        this.languageCode = description.getLanguageCode();
        try {
            languageData = (JsonObject) JsonUtils.decode(Utils.readFromInputStream(description.getLanguageFile()), JsonObject.class);
        } catch (Exception exception) {
            org.nofs.sdkserver.getLogger().warn("Failed to load language file: " + description.getLanguageCode(), (Throwable) exception);
        }
        this.languageData = languageData;
    }

    private static LanguageStreamDescription getLanguageFileDescription(String languageCode, String fallbackLanguageCode) {
        String fileName = languageCode + ".json";
        String fallback = fallbackLanguageCode + ".json";
        String actualLanguageCode = languageCode;
        InputStream file = org.nofs.sdkserver.class.getResourceAsStream("/languages/" + fileName);
        if (file == null) {
            org.nofs.sdkserver.getLogger().warn("Failed to load language file: " + fileName + ", falling back to: " + fallback);
            actualLanguageCode = fallbackLanguageCode;
            if (cachedLanguages.containsKey(actualLanguageCode)) {
                return new LanguageStreamDescription(actualLanguageCode, null);
            }
            file = org.nofs.sdkserver.class.getResourceAsStream("/languages/" + fallback);
        }
        if (file == null) {
            org.nofs.sdkserver.getLogger().warn("Failed to load language file: " + fallback + ", falling back to: en-US.json");
            actualLanguageCode = "en-US";
            if (cachedLanguages.containsKey(actualLanguageCode)) {
                return new LanguageStreamDescription(actualLanguageCode, null);
            }
            file = org.nofs.sdkserver.class.getResourceAsStream("/languages/en-US.json");
        }
        if (file == null) {
            throw new RuntimeException("Unable to load the primary, fallback, and 'en-US' language files.");
        }
        return new LanguageStreamDescription(actualLanguageCode, file);
    }

    public String get(String key) {
        if (this.cachedTranslations.containsKey(key)) {
            return this.cachedTranslations.get(key);
        }
        String[] keys = key.split("\\.");
        JsonObject object = this.languageData;
        int index = 0;
        String result = "This value does not exist. Please report this to the Discord: " + key;
        boolean isValueFound = false;
        while (true) {
            if (index != keys.length) {
                int i = index;
                index++;
                String currentKey = keys[i];
                if (!object.has(currentKey)) {
                    break;
                }
                JsonElement element = object.get(currentKey);
                if (element.isJsonObject()) {
                    object = element.getAsJsonObject();
                } else {
                    isValueFound = true;
                    result = element.getAsString();
                    break;
                }
            } else {
                break;
            }
        }
        if (!isValueFound && !this.languageCode.equals("en-US")) {
            String englishValue = getLanguage("en-US").get(key);
            if (!englishValue.contains("This value does not exist. Please report this to the Discord: ")) {
                result = result + "\nhere is english version:\n" + englishValue;
            }
        }
        this.cachedTranslations.put(key, result);
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: org.nofs.jar:emu/org.nofs/utils/Language$LanguageStreamDescription.class */
    public static class LanguageStreamDescription {
        private final String languageCode;
        private final InputStream languageFile;

        public LanguageStreamDescription(String languageCode, InputStream languageFile) {
            this.languageCode = languageCode;
            this.languageFile = languageFile;
        }

        public String getLanguageCode() {
            return this.languageCode;
        }

        public InputStream getLanguageFile() {
            return this.languageFile;
        }
    }

    /* loaded from: org.nofs.jar:emu/org.nofs/utils/Language$TextStrings.class */
    public static class TextStrings implements Serializable {
        public static final String[] ARR_LANGUAGES = {"EN", "CHS", "CHT", "JP", "KR", "DE", "ES", "FR", "ID", "PT", "RU", "TH", "VI"};
        public static final String[] ARR_GC_LANGUAGES = {"en-US", "zh-CN", "zh-TW", "ja-JP", "ko-KR", "en-US", "es-ES", "fr-FR", "en-US", "en-US", "ru-RU", "en-US", "en-US"};
        public static final int NUM_LANGUAGES = ARR_LANGUAGES.length;
        public static final List<String> LIST_LANGUAGES = Arrays.asList(ARR_LANGUAGES);
        public static final Object2IntMap<String> MAP_LANGUAGES =  // Map "EN": 0, "CHS": 1, ..., "VI": 12
                new Object2IntOpenHashMap<>(
                        IntStream.range(0, ARR_LANGUAGES.length)
                                .boxed()
                                .collect(Collectors.toMap(i -> ARR_LANGUAGES[i], i -> i)));
        public static final Object2IntMap<String> MAP_GC_LANGUAGES =  // Map "en-US": 0, "zh-CN": 1, ...
                new Object2IntOpenHashMap<>(
                        IntStream.range(0, ARR_GC_LANGUAGES.length)
                                .boxed()
                                .collect(Collectors.toMap(i -> ARR_GC_LANGUAGES[i], i -> i, (i1, i2) -> i1)));
        public String[] strings;

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof TextStrings) {
                TextStrings other = (TextStrings) o;
                return other.canEqual(this) && Arrays.deepEquals(this.strings, other.strings);
            }
            return false;
        }

        protected boolean canEqual(Object other) {
            return other instanceof TextStrings;
        }

        public int hashCode() {
            int result = (1 * 59) + Arrays.deepHashCode(this.strings);
            return result;
        }

        public TextStrings() {
            this.strings = new String[ARR_LANGUAGES.length];
        }

        public TextStrings(String init) {
            this.strings = new String[ARR_LANGUAGES.length];
            for (int i = 0; i < NUM_LANGUAGES; i++) {
                this.strings[i] = init;
            }
        }

        public TextStrings(List<String> strings, int key) {
            this.strings = new String[ARR_LANGUAGES.length];
            String nullReplacement = "[N/A] %d".formatted(new Object[]{Long.valueOf(key & 4294967295L)});
            int i = 0;
            while (true) {
                if (i >= NUM_LANGUAGES) {
                    break;
                }
                String s = strings.get(i);
                if (s == null) {
                    i++;
                } else {
                    nullReplacement = "[%s] - %s".formatted(new Object[]{ARR_LANGUAGES[i], s});
                    break;
                }
            }
            for (int i2 = 0; i2 < NUM_LANGUAGES; i2++) {
                String s2 = strings.get(i2);
                if (s2 != null) {
                    this.strings[i2] = s2;
                } else {
                    this.strings[i2] = nullReplacement;
                }
            }
        }

        public static List<Language> getLanguages() {
            return Arrays.stream(ARR_GC_LANGUAGES).map(Language::getLanguage).toList();
        }

        public String get(int languageIndex) {
            return this.strings[languageIndex];
        }

        public String get(String languageCode) {
            return this.strings[MAP_LANGUAGES.getOrDefault(languageCode, 0)];
        }

        public String getGC(String languageCode) {
            return this.strings[MAP_GC_LANGUAGES.getOrDefault(languageCode, 0)];
        }

        public boolean set(String languageCode, String string) {
            int index = MAP_LANGUAGES.getOrDefault(languageCode, -1);
            if (index < 0) {
                return false;
            }
            this.strings[index] = string;
            return true;
        }
    }
}
