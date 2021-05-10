package bookclub.technion.maymsgphoto.adapters;

/**
 * Created by a on 5/14/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.cunoraz.gifview.library.GifView;

import java.util.ArrayList;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.main.ChatActivity;
import bookclub.technion.maymsgphoto.main.NotificationListActivity;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;

/**
 * Created by a on 4/27/2017.
 */

public class NotiUserListAdapter extends BaseAdapter {

    private NotificationListActivity _context;
    private ArrayList<UserEntity> _datas = new ArrayList<>();
    private ArrayList<UserEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public NotiUserListAdapter(NotificationListActivity context){

        super();
        this._context = context;

        _imageLoader = MayMsgPhotoApplication.getInstance().getImageLoader();
    }

    public void setDatas(ArrayList<UserEntity> datas) {

        _alldatas = datas;
        _datas.clear();
        _datas.addAll(_alldatas);
    }

    @Override
    public int getCount(){
        return _datas.size();
    }

    @Override
    public Object getItem(int position){
        return _datas.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        final CustomHolder holder;

        if (convertView == null) {
            holder = new CustomHolder();

            LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.noti_user_list, parent, false);

            holder.photo = (CircularNetworkImageView) convertView.findViewById(R.id.photo);
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.email=(TextView)convertView.findViewById(R.id.email);
            holder.secret=(TextView)convertView.findViewById(R.id.secret);
            holder.datetime=(TextView)convertView.findViewById(R.id.datetime);
            holder.gif=(GifView)convertView.findViewById(R.id.gif);
            holder.gif2=(GifView)convertView.findViewById(R.id.gif2);
            holder.gif3=(GifView)convertView.findViewById(R.id.gif3);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _datas.get(position);

        if(user.get_name().length()>0)
            holder.name.setText(user.get_name());
        else
            holder.name.setText(user.get_fullName());

//        holder.email.setVisibility(View.VISIBLE);
        holder.email.setText(user.get_email());
        holder.datetime.setText("");

        Log.d("User===>", user.get_name());
        Log.d("Regular===>", user.getRegular());
        Log.d("Secret===>", user.getSecret());
        Log.d("Carmode===>", user.getCarmode());

        if(user.getCarmode().length()>0) {
            holder.gif3.setVisibility(View.VISIBLE);
            holder.gif3.play();
        }else {
            holder.gif3.setVisibility(View.GONE);
        }
        if(user.getSecret().length()>0) {
            holder.gif2.setVisibility(View.VISIBLE);
            holder.gif2.play();
        }else {
            holder.gif2.setVisibility(View.GONE);
        }
        if(user.getRegular().length()>0)  {
            holder.gif.setVisibility(View.VISIBLE);
            holder.gif.play();
        }else holder.gif.setVisibility(View.GONE);

        if(user.get_photoUrl().length()>0) {
            holder.photo.setImageUrl(user.get_photoUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.userEntity=new UserEntity();
                Commons.userEntity=user;

                Intent intent=new Intent(_context, ChatActivity.class);
                _context.startActivity(intent);
                _context.finish();

            }
        });

        return convertView;
    }

    public void filter(String charText){

        charText = charText.toLowerCase();
        _datas.clear();

        if(charText.length() == 0){
            _datas.addAll(_alldatas);
        }else {

            for (UserEntity userEntity : _alldatas){

                if (userEntity instanceof UserEntity) {

                    String value = ((UserEntity) userEntity).get_fullName().toLowerCase();
                    String value1 = ((UserEntity) userEntity).get_name().toLowerCase();
                    if (value.contains(charText) || value1.contains(charText)) {
                        _datas.add(userEntity);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    class CustomHolder {

        public CircularNetworkImageView photo;
        public TextView name;
        public TextView email;
        public TextView secret;
        public TextView datetime;
        public GifView gif;
        public GifView gif2;
        public GifView gif3;
    }
}






