package com.camera;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class ImageRecyclerAdapter extends RecyclerView.Adapter<ImageRecyclerAdapter.ImageViewHolder> {

    private final List<ImageUriModal> uriList;
    private final Context context;
    public ImageRecyclerAdapter(List<ImageUriModal> uriList, Context context) {
        this.uriList = uriList;
        this.context = context;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.set_multiple_image_layout,viewGroup,false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder imageViewHolder, int i) {
    ImageUriModal imageUriModal = uriList.get(i);
    //imageViewHolder.multipleImg.setImageURI(imageUriModal.getImgUri());
//        set the image in cicular form by glide
        Glide.with(context).load(imageUriModal.getImgUri()).apply(RequestOptions.circleCropTransform())
                          .placeholder(R.drawable.loader).into(imageViewHolder.multipleImg);
//        for change the size of the image in glide
       /* Glide.with(context).load(imageUriModal.getImgUri()).apply(new RequestOptions().override(600, 200))
                .placeholder(R.drawable.loader).into(imageViewHolder.multipleImg);*/

    }

    @Override
    public int getItemCount() {
        return uriList.size();
    }

    public class ImageViewHolder  extends RecyclerView.ViewHolder {
        ImageView multipleImg;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            multipleImg = itemView.findViewById(R.id.from_gallery_img);
        }
    }
}
