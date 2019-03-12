package be.kdg.mobile_client.friends;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import be.kdg.mobile_client.R;
import be.kdg.mobile_client.user.UserActivity;
import be.kdg.mobile_client.user.model.User;
import lombok.AllArgsConstructor;

/**
 * An adapter that is using the recycler method.
 * The class extends the generic inner class implementation of the ViewHolder.
 */
@AllArgsConstructor
public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {
    private final Context ctx;
    private final List<User> users;

    /**
     * Inflates the layout that will be used to display each friend.
     *
     * @return a ViewHolder that is based on the inflated view.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_friend, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Binds all the information of one friend to one of the view holders.
     * When clicking on the name -> navigate to user account
     * When clicking on the delete button -> delete friend
     *
     * @param holder   The holder that "holds" the views that are created so they can be recycled.
     * @param position The position in the array.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvName.setText(user.getUsername());

        /*
        holder.tvName.setOnClickListener(e -> {
            Intent intent = new Intent(ctx, UserActivity.class);
            intent.putExtra(ctx.getString(R.string.userid), user.getId());
            ctx.startActivity(intent);
        });

        holder.btnRemoveFriend.setOnClickListener(e -> {
            if (ctx instanceof  FriendsActivity) {
                ((FriendsActivity) ctx).removeFriend(user);
            }
        });
        */
    }

    /**
     * Used internally by the recycler to determine how many holders should be created.
     *
     * @return The size of the users list.
     */
    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * This inner class will hold the layouts in memory so that
     * they can be recycled.
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        Button btnRemoveFriend;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvFriendsRow);
            btnRemoveFriend = itemView.findViewById(R.id.btnRemoveFriend);
        }
    }
}
