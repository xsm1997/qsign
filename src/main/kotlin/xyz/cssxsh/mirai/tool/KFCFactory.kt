package xyz.cssxsh.mirai.tool

import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.*
import kotlinx.serialization.json.*
import net.mamoe.mirai.internal.spi.*
import net.mamoe.mirai.internal.utils.*
import net.mamoe.mirai.utils.*

public class KFCFactory : EncryptService.Factory {
    public companion object {
        @JvmStatic
        public fun install() {
            Services.register(
                EncryptService.Factory::class.qualifiedName!!,
                KFCFactory::class.qualifiedName!!,
                ::KFCFactory
            )
        }

        @JvmStatic
        public val DEFAULT_CONFIG: String = """
            {
                "0.0.0": {
                    "base_url": "http://127.0.0.1:8080",
                    "type": "fuqiuluo/unidbg-fetch-qsign",
                    "key": "114514"
                },
                "0.1.0": {
                    "base_url": "http://127.0.0.1:8888",
                    "type": "kiliokuara/magic-signer-guide",
                    "serverIdentityKey": "vivo50",
                    "authorizationKey": "kfc"
                },
                "8.8.88": {
                    "base_url": "http://127.0.0.1:80",
                    "type": "TLV544Provider"
                }
            }
        """.trimIndent()
    }

    @Suppress("INVISIBLE_MEMBER")
    override fun createForBot(context: EncryptServiceContext, serviceSubScope: CoroutineScope): EncryptService {
        try {
            org.asynchttpclient.Dsl.config()
        } catch (cause: NoClassDefFoundError) {
            throw RuntimeException("请参照 https://search.maven.org/artifact/org.asynchttpclient/async-http-client/2.12.3/jar 添加依赖", cause)
        }
        return when (val protocol = context.extraArgs[EncryptServiceContext.KEY_BOT_PROTOCOL]) {
            BotConfiguration.MiraiProtocol.ANDROID_PHONE, BotConfiguration.MiraiProtocol.ANDROID_PAD -> {
                val impl = MiraiProtocolInternal[protocol]

                val server = with(java.io.File("KFCFactory.json")) {
                    if (exists().not()) {
                        writeText(DEFAULT_CONFIG)
                    }
                    val serializer = MapSerializer(String.serializer(), ServerConfig.serializer())
                    val servers = Json.decodeFromString(serializer, readText())
                    servers[impl.ver]
                        ?: throw NoSuchElementException("没有找到对应 ${impl.ver} 的服务配置，${toPath().toUri()}")
                }

                when (val type = server.type.ifEmpty { throw IllegalArgumentException("need server type") }) {
                    "fuqiuluo/unidbg-fetch-qsign", "fuqiuluo", "unidbg-fetch-qsign" -> UnidbgFetchQsign(
                        server = server.base,
                        key = server.key,
                        coroutineContext = serviceSubScope.coroutineContext
                    )
                    "kiliokuara/magic-signer-guide", "kiliokuara", "magic-signer-guide", "vivo50" -> ViVo50(
                        server = server.base,
                        serverIdentityKey = server.serverIdentityKey,
                        authorizationKey = server.authorizationKey,
                        coroutineContext = serviceSubScope.coroutineContext
                    )
                    "TLV544Provider" -> TLV544Provider()
                    else -> throw UnsupportedOperationException(type)
                }
            }
            BotConfiguration.MiraiProtocol.ANDROID_WATCH -> throw UnsupportedOperationException(protocol.name)
            BotConfiguration.MiraiProtocol.IPAD, BotConfiguration.MiraiProtocol.MACOS -> TLV544Provider()
        }
    }
}

@Serializable
private data class ServerConfig(
    @SerialName("base_url")
    val base: String,
    @SerialName("type")
    val type: String = "",
    @SerialName("key")
    val key: String = "",
    @SerialName("serverIdentityKey")
    val serverIdentityKey: String = "",
    @SerialName("authorizationKey")
    val authorizationKey: String = ""
)