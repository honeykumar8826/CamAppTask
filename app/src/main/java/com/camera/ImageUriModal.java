package com.camera;

import android.net.Uri;

public class ImageUriModal {
    private Uri imgUri;

    public ImageUriModal(Uri imgUri) {
        this.imgUri = imgUri;
    }

    public Uri getImgUri() {
        return imgUri;
    }

/*    public void setImgUri(Uri imgUri) {
        this.imgUri = imgUri;
    }*/

}
