<div align="center">

# Photo Search

A simple android app to search for images over the internet

<img src="https://user-images.githubusercontent.com/22789194/29027804-bb5b8178-7b71-11e7-9672-a5c624521ee8.jpeg">



</div>

### API Used

[Google custom search api](https://developers.google.com/custom-search/json-api/v1/overview) - To receive images from all possible sources upon request

### Libraries Used
  - [Volley](https://github.com/google/volley) - For network call and caching
  - [Glide](https://github.com/bumptech/glide) - For specifically image requests and caching


### Installing

If the application doesn't work, add your own key following the [api-guide](https://developers.google.com/custom-search/json-api/v1/overview) and insert them into [QueryUtils file](https://github.com/nobodyme/photo-search/blob/master/app/src/main/java/com/example/imnobody/photosearch/QueryUtils.java) in the following field mentioned below

`uriBuilder.appendQueryParameter("key", "YOUR-API-KEY-HERE");`
  
### License

MIT

