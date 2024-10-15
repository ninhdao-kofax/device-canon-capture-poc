var captionKey = "";
window.onload = function() {
  console.log("Cooperation to JavaScript Printix Canon print page was loaded.");
  initUI();
};


function initUI(){
  translate();
}
function translate(){
  initCaptions();
  var permission = document.getElementById("permission-denied");
  if(permission != null){
    permission.innerText = captions.ACCESS_DENIED;
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
          ACCESS_DENIED: "Permission denied",
      }
  }, {
      LanguageCode: "da",
      messages: {
          ACCESS_DENIED: "Tilladelse nægtet",
      }
  }, {
      LanguageCode: "de",
      messages: {
          ACCESS_DENIED: "Zugriff verweigert",
      }
  }, {
      LanguageCode: "es",
      messages: {
          ACCESS_DENIED: "Permiso denegado",
      }
  }, {
      LanguageCode: "fr",
      messages: {
          ACCESS_DENIED: "Permission refusée",
      }
  }, {
      LanguageCode: "it",
      messages: {
          ACCESS_DENIED: "Permesso negato",
      }
  }, {
      LanguageCode: "nl",
      messages: {
          ACCESS_DENIED: "Toestemming geweigerd",
      }
  }, {
      LanguageCode: "no",
      messages: {
          ACCESS_DENIED: "Tillatelse avslått",
      }
  }, {
      LanguageCode: "pl",
      messages: {
          ACCESS_DENIED: "Odmowa uprawnień",
      }
  }, {
      LanguageCode: "pt",
      messages: {
          ACCESS_DENIED: "Permissão negada",
      }
  }, {
      LanguageCode: "ro",
      messages: {
          ACCESS_DENIED: "Permisiune refuzată",
      }
  }, {
      LanguageCode: "fi",
      messages: {
          ACCESS_DENIED: "Käyttö estetty",
      }
  }, {
      LanguageCode: "sv",
      messages: {
          ACCESS_DENIED: "Åtkomst nekad",
      }
  }, {
      LanguageCode: "ar",
      messages: {
          ACCESS_DENIED: "تم رفض الإذن",
      }
  }, {
      LanguageCode: "zh-cn",
      messages: {
          ACCESS_DENIED: "权限被拒绝",
      }
  }, {
      LanguageCode: "zh-tw",
      messages: {
          ACCESS_DENIED: "權限遭拒",
      }
  }, {
      LanguageCode: "he",
      messages: {
          ACCESS_DENIED: "ההרשאה נדחתה",
      }
  }, {
      LanguageCode: "jp",
      messages: {
          ACCESS_DENIED: "アクセス拒否",
      }
  }, {
      LanguageCode: "ko",
      messages: {
          ACCESS_DENIED: "권한이 거부됨",
      }
  } ]
};