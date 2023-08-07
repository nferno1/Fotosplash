package nferno1.fotosplash.api

enum class Scope constructor(val scope: String) {

    PUBLIC("public"),
    READ_USER("read_user"),
    WRITE_USER("write_user"),
    WRITE_LIKES("write_likes")
}
