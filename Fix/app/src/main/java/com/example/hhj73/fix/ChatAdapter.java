package com.example.hhj73.fix;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.RegionIterator;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sun.mail.imap.Rights;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends BaseAdapter {

    List<ChatData> m_data;
    Context context;
    String myId;
    String myName;
    String urName;
    LinearLayout chatBox;
    LinearLayout chatLayout;
    LinearLayout nameBox;
    TextView nameText;

    public ChatAdapter(Context context, ArrayList<ChatData> data, String myId, String myName, String urName) {
        this.context = context;
        this.m_data = data;
        this.myId = myId;
        this.myName = myName;
        this.urName = urName;
    }

    @Override
    public int getCount() {
        return m_data.size();
    }

    @Override
    public Object getItem(int i) {
        return m_data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        context = parent.getContext();

        /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.mychat, parent, false);
        }

        /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
        TextView message = (TextView) convertView.findViewById(R.id.c_message);
        TextView time = (TextView) convertView.findViewById(R.id.c_time);
        nameBox = (LinearLayout) convertView.findViewById(R.id.nameBox);
        nameText = (TextView)convertView.findViewById(R.id.nameText);
        chatLayout = (LinearLayout)convertView.findViewById(R.id.chatLayout);
        chatBox = (LinearLayout)convertView.findViewById(R.id.chatBox);

        /* 각 위젯에 세팅된 아이템을 뿌려준다 */
        message.setText(m_data.get(position).getMessage());
        time.setText(m_data.get(position).getTime());


        // 색깔 여기서 하시오
        if(m_data.get(position).getUserName().equals(myId)) {
            // 내가 보낸 메시지이면
            nameText.setText(myName);
            chatBox.setGravity(Gravity.LEFT);
            nameBox.setGravity(Gravity.RIGHT);
            chatLayout.setBackgroundResource(R.drawable.chat_right);
        }
        else {
            // 상대가 보낸 메시지이면
            nameText.setText(urName);
            chatBox.setGravity(Gravity.RIGHT);
            nameBox.setGravity(Gravity.LEFT);
            chatLayout.setBackgroundResource(R.drawable.chat_left);
            //convertView.setBackgroundColor(Color.rgb(250, 250, 210)); // 하얀색 배경
        }


//        /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */

        return convertView;

    }
}
