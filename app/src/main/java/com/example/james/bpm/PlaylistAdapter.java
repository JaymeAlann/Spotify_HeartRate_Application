package com.example.james.bpm;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    private static final String TAG = "PlaylistAdapter";

    private Context context;
    private int mResource;
    private int lastPosition = -1;


    static class ViewHolder{
        TextView name;
        TextView owner;
        ImageView image;
    }

    /**
     * DEFAULT CONSTRUCTOR FOR PLAYLIST ADAPTER
     * @param context
     * @param resource
     * @param objects
     */
    public PlaylistAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Playlist> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //CALL IMAGE LOADER

        //GET PLAYLIST INFORMATION
        String playlistName = getItem(position).getName();
        String playlistOwner = getItem(position).getOwner().getPlaylistOwnerName();
        String playlistImageUrl = getItem(position).getPlaylistImage().get(0).getUrl();

        //CREATE VIEW THAT WILL SHOW THE ANIMATION
        final View result;

        //VIEWHOLDER OBJECT
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.name = convertView.findViewById(R.id.PlaylistName);
            holder.owner = convertView.findViewById(R.id.PlaylistOwner);
            holder.image = convertView.findViewById(R.id.PlaylistImage);

            result = convertView;
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(context,
                (position > lastPosition) ? R.anim.load_down_anim: R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition=position;

        int defautlImg = context.getResources().getIdentifier("@drawable/image_failed",null,context.getPackageName());

        // IMAGE LOADER INSTANTIATION
        loadImageFromUrl(playlistImageUrl,holder.image);
        holder.name.setText(playlistName);
        holder.owner.setText(playlistOwner);

        return convertView;
    }

    private void loadImageFromUrl(String url, ImageView holder){
        Picasso.with(this.context).load(url).placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder, new com.squareup.picasso.Callback(){

                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

                    }
                });
    }
}
