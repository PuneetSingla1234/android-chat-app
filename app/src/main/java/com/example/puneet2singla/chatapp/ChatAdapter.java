package com.example.puneet2singla.chatapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    Context context;
    List<ChatMessage> chatMessages;

    public ChatAdapter(Context context,List<ChatMessage> chatMessages){
        this.context=context;
        this.chatMessages=chatMessages;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_chat, null);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage chatMessage=chatMessages.get(position);
        String msg = chatMessage.getMsg();
        Log.d("Printed",msg);
        holder.txtMessage.setText(msg);

        int color = (Color.BLUE);


        if (!chatMessage.isSentByMe()) {

            color =(Color.MAGENTA);
        }
        else{
            RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) holder.txtMessage.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
        Log.d("Color",msg+" "+color);


    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.txtMessage)
        TextView txtMessage;
        @BindView(R.id.relativeLayout)
        RelativeLayout relativeLayout;
        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private boolean alreadyInAdapter(ChatMessage newMsg){
        boolean alreadyInAdapter = false;
        for (ChatMessage msg : this.chatMessages) {
            if (msg.getKey().equals(newMsg.getKey()))  {
                alreadyInAdapter = true;
                break;
            }
        }

        return alreadyInAdapter;
    }

    public void add(ChatMessage message) {
        if (!alreadyInAdapter(message)) {
            chatMessages.add(message);
            notifyDataSetChanged();
        }
    }
}
