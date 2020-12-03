package com.example.smartsumparking.helpers;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smartsumparking.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private static final String TAG = "RecyclerViewAdapter";

    private List<String> mEmails;
    private List<String> mImages;
    private List<Boolean> mRoles;
    private List<String> mIds;
    private ImageView delete;
    private Context mContext;
    private FirebaseFirestore db;

    public RecyclerViewAdapter(List<String> mEmails, List<String> mImages, List<Boolean> mRoles, List<String> mIds, Context mContext) {
        this.mEmails = mEmails;
        this.mImages = mImages;
        this.mRoles = mRoles;
        this.mIds=mIds;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_list_users, parent, false);

        db=FirebaseFirestore.getInstance();

        ViewHolder holder = new ViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImages.get(position))
                .into(holder.image);
        holder.mEmail.setText(mEmails.get(position));
        holder.checkBox.setChecked(mRoles.get(position));

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {

                if(v.getId()== R.id.checkbox){
                    CheckBox chk = (CheckBox)v;

                    if(chk.isChecked()){
                        db.collection("users").document(mIds.get(position))
                                .update("roles.admin", true);
                    }else if(!chk.isChecked()){
                        db.collection("users").document(mIds.get(position))
                                .update("roles.admin", false);
                    }
                }else if(v.getId()== R.id.delete){
                    ImageView im = (ImageView)v;

                    db.collection("users").document(mIds.get(position))
                            .delete();

                    notifyItemRemoved(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEmails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CircleImageView image;
        RelativeLayout parentLayout;
        TextView mEmail;
        CheckBox checkBox;
        ItemClickListener itemClickListener;


        public ViewHolder(View itemView) {
            super(itemView);

            image = (CircleImageView)itemView.findViewById(R.id.image);
            checkBox = (CheckBox)itemView.findViewById(R.id.checkbox);
            parentLayout = (RelativeLayout)itemView.findViewById(R.id.layout_parent);
            mEmail = (TextView)itemView.findViewById(R.id.email);
            delete = (ImageView) itemView.findViewById(R.id.delete);

            checkBox.setOnClickListener(this);
            delete.setOnClickListener(this);
        }

        public void setItemClickListener(ItemClickListener ic){
            this.itemClickListener = ic;
        }

        @Override
        public void onClick(View v) {
            this.itemClickListener.onItemClick(v, getLayoutPosition());
        }
    }
}
