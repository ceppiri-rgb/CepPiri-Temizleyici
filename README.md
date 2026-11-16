# CepPiri Temizleyici

Minimal, reklamsız, izin istemeyen Android önbellek temizleyici.

## Özellikler
- Uygulamanın kendi dahili/harici cache dizinlerini temizler.
- Telefon boş/toplam depolama miktarını gösterir.
- Telefon depolama ayarlarına ve uygulama listesine götürür.
- Android 6+ (API 23), Java, XML layout bağımlılığı yok.
- İnternet, dosya erişimi veya özel izin istemez.

**Önemli:** Android üçüncü taraf uygulamalara diğer uygulamaların cache dosyalarını otomatik temizleme yetkisi vermez. Bu uygulama RAM boşaltmaz ve diğer uygulamaların önbelleğini silmiş gibi davranmaz.

## APK üretme

**Android Studio:** Bu klasörü Android Studio'da açın, JDK 17 kullanın ve Build > Build APK(s) seçin. Çıktı: `app/build/outputs/apk/debug/app-debug.apk`.

**GitHub Actions:** Projeyi bir GitHub deposuna yükleyin. Actions > Android APK > Run workflow. İşlem bitince `CepPiri-Temizleyici-APK` adlı artifact'i indirin ve ZIP içindeki APK'yı alın.

Bu paket doğrudan APK değildir; kaynak kod ve otomatik APK derleme yapılandırması içerir.
