var captionKey = "";
window.onload = function() {
  console.log("Cooperation to JavaScript Printix Canon Capture page was loaded.");
  initUI();
};


function initUI(){
  translate();
}
function translate(){
  initCaptions();
  var noconfig = document.getElementById("no-configuration");
  if(noconfig != null){
    noconfig.innerText = captions.ONDEVICE_CONFIG_NOT_FOUND;
  }
}

/**
 * For Localize Section
 */
var captions = null;
function initCaptions() {
    captions = setCaptions();
}

var supportedLanguages = {
    en: "en",
    da: "da",
    de: "de",
    es: "es",
    fr: "fr",
    it: "it",
    sv: "sv",
    nb: "no",
    nn: "no",
    no: "no",
    nl: "nl",
    fi: "fi",
    pl: "pl",
    pt: "pt",
    ro: "ro",
    ar: "ar",
    zh: "zh-cn",
    zh_tw: "zh-tw",
    he: "he",
    ja: "jp",
    jp: "jp",
    ko: "ko"
};

function setCaptionKey() {
  var captionKey = null;
  captionKey = navigator.language || navigator.userLanguage;
  if (captionKey === "zh-TW" || captionKey === "zh-tw") {
    captionKey = "zh_tw";
  }
  captionKey = captionKey.split("-")[0];
  captionKey = supportedLanguages[captionKey];
  if (!captionKey) {
    captionKey = "en";
  }
  return captionKey;
}

function setCaptions() {
    var userLang = setCaptionKey();
    for (var i = 0; i < captions.Captions.length; i++) {
        if (captions.Captions[i].LanguageCode === userLang) {
            var caption = captions.Captions[i];
            return captionPackage = caption.messages;
        }
    }
}

var captions = {
  Captions: [ {
      LanguageCode: "en",
      messages: {
        ONDEVICE_CONFIG_NOT_FOUND: "Go configuration not found",
      }
  }, {
      LanguageCode: "da",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-konfiguration ikke fundet",
      }
  }, {
      LanguageCode: "de",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-Konfiguration nicht gefunden",
      }
  }, {
      LanguageCode: "es",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Configuración de Go no encontrada",
      }
  }, {
      LanguageCode: "fr",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Configuration Go introuvable",
      }
  }, {
      LanguageCode: "it",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Configurazione Go non trovata",
      }
  }, {
      LanguageCode: "nl",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-configuratie niet gevonden",
      }
  }, {
      LanguageCode: "no",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-konfigurasjon ikke funnet",
      }
  }, {
      LanguageCode: "pl",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Nie znaleziono konfiguracji Go",
      }
  }, {
      LanguageCode: "pt",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Configuração Go não encontrada",
      }
  }, {
      LanguageCode: "ro",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Configurația Go nu a fost găsită",
      }
  }, {
      LanguageCode: "fi",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-konfigurointia ei löytynyt",
      }
  }, {
      LanguageCode: "sv",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go-konfiguration hittades inte",
      }
  }, {
      LanguageCode: "ar",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "لم يتم العثور على التكوين Go",
      }
  }, {
      LanguageCode: "zh-cn",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "找不到 Go 配置",
      }
  }, {
      LanguageCode: "zh-tw",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "找不到 Go 組態",
      }
  }, {
      LanguageCode: "he",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "תצורת Go לא נמצאה",
      }
  }, {
      LanguageCode: "jp",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go 構成が見つかりません",
      }
  }, {
      LanguageCode: "ko",
      messages: {
          ONDEVICE_CONFIG_NOT_FOUND: "Go 구성을 찾을 수 없음",
      }
  } ]
};