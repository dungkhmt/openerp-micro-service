import i18n from "i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import {initReactI18next} from "react-i18next";
import Backend from "i18next-http-backend";

i18n
  .use(LanguageDetector)
  .use(initReactI18next)
  .use(Backend)
  .init({
    fallbackLng: "en",
    debug: true,

    // have a common namespace used around the full app
    // ns: ["translations"],
    // defaultNS: "translations",

    interpolation: {
      escapeValue: false,
    },
  });

export default i18n;
