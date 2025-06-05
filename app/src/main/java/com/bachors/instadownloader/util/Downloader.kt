package com.bachors.instadownloader.util

interface Downloader {
    fun downloadFile(url: String, name: String, type: String): Long
}