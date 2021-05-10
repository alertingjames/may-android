package bookclub.technion.maymsgphoto.adapters;

/**
 * Created by a on 5/13/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

import bookclub.technion.maymsgphoto.MayMsgPhotoApplication;
import bookclub.technion.maymsgphoto.R;
import bookclub.technion.maymsgphoto.commons.Commons;
import bookclub.technion.maymsgphoto.main.UserProfileActivity;
import bookclub.technion.maymsgphoto.models.UserEntity;
import bookclub.technion.maymsgphoto.utils.CircularNetworkImageView;

/**
 * Created by a on 4/27/2017.
 */

public class ChatUserListAdapter extends BaseAdapter {

    private Context _context;
    private ArrayList<UserEntity> _datas = new ArrayList<>();
    private ArrayList<UserEntity> _alldatas = new ArrayList<>();

    ImageLoader _imageLoader;

    public ChatUserListAdapter(Context context){

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
            convertView = inflater.inflate(R.layout.chat_user_list_item, parent, false);

            holder.photo = (CircularNetworkImageView) convertView.findViewById(R.id.photo);
            holder.name=(TextView)convertView.findViewById(R.id.name);
            holder.email=(TextView)convertView.findViewById(R.id.email);
            holder.num=(TextView)convertView.findViewById(R.id.num);
            holder.badge=(ImageView)convertView.findViewById(R.id.badge);

            convertView.setTag(holder);
        } else {
            holder = (CustomHolder) convertView.getTag();
        }

        final UserEntity user = (UserEntity) _datas.get(position);

        if(user.get_name().length()>0)
            holder.name.setText(user.get_name());
        else
            holder.name.setText(user.get_fullName());

        holder.email.setVisibility(View.VISIBLE);
        holder.email.setText(user.get_email());

        Log.d("Photo11===>",user.get_photoUrl());
        if(user.get_photoUrl().length()>0) {
            holder.photo.setImageUrl(user.get_photoUrl(),_imageLoader);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Commons.userEntity=new UserEntity();
                Commons.userEntity=user;
                Intent intent=new Intent(_context, UserProfileActivity.class);
                _context.startActivity(intent);
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
        public TextView num;
        public ImageView badge;
    }
}





