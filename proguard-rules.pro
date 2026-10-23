# BouncyCastle is loaded by sshj reflectively via Class.forName(), so R8 cannot see
# the reference and strips the provider classes. Without them sshj falls back to the
# platform JCE provider, where X25519 key exchange fails on Android with
# "To generate a key pair in Android Keystore, use KeyPairGenerator initialized with
# android.security.keystore.KeyGenParameterSpec".
# See upstream commit f6066cd which added the same rules for ECDSA.
-keep class org.bouncycastle.jcajce.provider.** { *; }
-keep class org.bouncycastle.jce.provider.** { *; }

-dontwarn javax.naming.**
-dontwarn org.slf4j.**
-dontwarn sun.security.x509.**

# Gson relies on generic type information and @SerializedName.
-keepattributes Signature
-keepattributes *Annotation*
-keep class * extends com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken
