package com.example.puneet2singla.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.ViewHolder> {
    @NonNull
    Context context;
    List<User> users;
    GlideImageLoader glideImageLoader;
    public ContactListAdapter(Context context, List<User> users,GlideImageLoader glideImageLoader) {
        this.context = context;
        this.users=users;
        this.glideImageLoader=glideImageLoader;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_contact, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user=users.get(position);
        String status=user.isOnline()?"online":"offline";
        holder.txtStatus.setText(status);
        int color=user.isOnline()?Color.GREEN:Color.RED;
        holder.txtStatus.setTextColor(color);
        holder.txtUser.setText(user.getEmail());
        glideImageLoader.load(holder.imgAvatar,AvatarHelper.getAvatarUrl(user.getEmail()));
    }



    @Override
    public int getItemCount() {
        return users.size();
    }

     class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imgAvatar)
        CircleImageView imgAvatar;
        @BindView(R.id.txtUser)
        TextView txtUser;
        @BindView(R.id.txtStatus)
        TextView txtStatus;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            User user=users.get(getAdapterPosition());
            Intent intent=new Intent(context,ChatActivity.class);
            intent.putExtra("Receiver",user.getEmail());
            intent.putExtra("IsOnline",user.isOnline());
            context.startActivity(intent);
        }
    }

    public boolean isAlreadyPresent(User newUser){
        boolean present=false;
        for(User user:users){
            if(user.getEmail().equals(newUser.getEmail())){
                present=true;
                break;
            }
        }
        return present;
    }

    public void update(User user) {
        int pos = getPosition(user);
        users.set(pos, user);
        notifyDataSetChanged();
    }

    public void addUser(User user){
        if(!isAlreadyPresent(user)){
            users.add(user);
            notifyDataSetChanged();
        }
    }
    public int getPosition(User reqdUser){
        int i=0;
        for(User user:users){
            if(user.getEmail().equals(reqdUser.getEmail())){
                break;
            }
            i++;
        }
        return i;
    }

    public void removeUser(User user){
        if(isAlreadyPresent(user)){
            users.remove(getPosition(user));
            notifyDataSetChanged();
        }
    }


}
