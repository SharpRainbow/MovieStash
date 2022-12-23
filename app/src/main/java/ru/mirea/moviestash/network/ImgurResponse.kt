package ru.mirea.moviestash.network

data class ImgurResponse(val data: Data?, val status: Int?, val success: Boolean?) {
    fun getImageLink() = data?.link
}

data class Data(val link: String?)