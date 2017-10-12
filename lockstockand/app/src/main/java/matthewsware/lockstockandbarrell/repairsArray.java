package matthewsware.lockstockandbarrell;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static matthewsware.lockstockandbarrell.R.id;

/**
 * Created by dylanmatthews on 2017/10/09.
 */

//this class makes the grid view, downloads the image

public class repairsArray extends ArrayAdapter<repairs> {

    private List<repairs> cardList = new ArrayList<repairs>();
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    public repairsArray(Context context, int resource) {
        super(context, resource);
    }

    static class CardViewHolder {

        ImageView im;
        String imgUrl;
        TextView tv;


    }

    @Override
    public void add(repairs object) {
        cardList.add(object);
        super.add(object);
    }

    @Override
    public int getCount() {
        return this.cardList.size();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final CardViewHolder viewHolder;
        if (row == null) {
            Log.i("shitTest", "shitTespoiuytt");
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.grid_image, parent, false);

            viewHolder = new CardViewHolder();
            viewHolder.im = (ImageView) row.findViewById(id.grid_item_image);
          //  viewHolder.tv = (TextView) row.findViewById(id.grid_item_text);

            row.setTag(viewHolder);
        } else {
            viewHolder = (CardViewHolder) row.getTag();
        }

        repairs card = getItem(position);
        viewHolder.imgUrl= card.getImgUrl();
        //downloads image
      //  viewHolder.tv.setText("jdshjds");
        String ref = "repairs/" + viewHolder.imgUrl;
        storageRef.child(ref).getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Use the bytes to display the image
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0
                        , bytes.length);

                viewHolder.im.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("shitTest", "shitTestp");
                        viewHolder.im.setImageBitmap(bitmap);
                    }
                }) ;


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.i("shit", exception.getMessage());

            }
        });

        //URI uri = new URI("android.resource;//am.dx.varsityspecials.www.varsityspecials/" + R.mipmap.add_btn);
        //viewHolder.im.setImageURI(Uri.parse("android.resource;//am.dx.varsityspecials.www.varsityspecials/" + mipmap.add_btn));
        // AQuery aq =  new AQuery(etContext())).id(exploreViewHolder.getvProfilePic()).image(item.getUserProfilePicUrl().trim(), true, true, device_width, R.drawable.profile_background, aquery.getCachedImage(R.drawable.profile_background),0);


        //r

        return row;

    }

}
