package com.example.james.bpm;

import android.content.Context;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SongListAdapter extends ArrayAdapter<Song> {
    private static final String TAG = "PlaylistAdapter";

    private Context context;
    private int mResource;
    private int lastPosition = -1;

    static class ViewHolder{
        TextView songName;
        TextView artist;
        ImageView songImg;
    }

    /**
     * DEFAULT CONSTRUCTOR FOR PLAYLIST ADAPTER
     * @param context
     * @param resource
     * @param objects
     */
    public SongListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Song> objects) {
        super(context, resource, objects);
        this.context = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //CALL IMAGE LOADER

        //GET PLAYLIST INFORMATION
        String sName = getItem(position).getSongName();
        String sArtist = getItem(position).getAlbum().getAlbumArtists().get(0).getArtistName();
        String imageUrl = getItem(position).getAlbum().getAlbumImages().get(0).getUrl();

        //CREATE VIEW THAT WILL SHOW THE ANIMATION
        final View result;

        //VIEWHOLDER OBJECT
        ViewHolder holder;

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.songName = convertView.findViewById(R.id.PlaylistName);
            holder.artist = convertView.findViewById(R.id.PlaylistOwner);
            holder.songImg = convertView.findViewById(R.id.PlaylistImage);

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

        loadImageFromUrl(imageUrl,holder.songImg);
        holder.songName.setText(sName);
        holder.artist.setText(sArtist);

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
