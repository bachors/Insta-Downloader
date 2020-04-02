# Insta-Downloader
[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-InstaDownloader-red.svg?style=flat)](https://android-arsenal.com/details/1/6088)

Simple Video &amp; Photo Downloader or Repost for Instagram.

New
-----
- Supports downloading carousel media. 

![gif](http://i.giphy.com/l3fzQ8q7hqaQ2ppOE.gif)


Usage
-----
<< <a href='http://bachors.com/tools/instagram-accesstoken-generator'>Access Token Generator</a> >>
```java
...

// config
InstaDownloader insta = new InstaDownloader(this);
insta.setAccessToken("Instagram API Access Token");
insta.setDir("/download");

// get video or photo by url
insta.get("https://www.instagram.com/p/xxx");
```

Step by step
------------
- Open the InstaDownloader app
- Open the Instagram app
- Click on the Copy Share URL menu
- Click the download or repost button

MIT
-----
