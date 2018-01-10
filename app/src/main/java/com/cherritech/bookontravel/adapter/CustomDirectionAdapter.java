package com.cherritech.bookontravel.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cherritech.bookontravel.R;
import com.cherritech.bookontravel.model.DirectionAdapter;

import java.util.List;

@SuppressWarnings("deprecation")
public class CustomDirectionAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<DirectionAdapter> directionAdapterList;

    public CustomDirectionAdapter(Activity activity, List<DirectionAdapter> directionAdapterList) {
        this.activity = activity;
        this.directionAdapterList = directionAdapterList;
    }

    @Override
    public int getCount() {
        return directionAdapterList.size();
    }

    @Override
    public Object getItem(int location) {
        return directionAdapterList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.layout_list_directions, null);

        TextView list_direction = convertView.findViewById(R.id.list_direction);
        TextView list_direction_time_distance = convertView.findViewById(R.id.list_direction_time_distance);
        ImageView image_direction = convertView.findViewById(R.id.image_direction);

        final DirectionAdapter directionAdapter = directionAdapterList.get(position);

        list_direction.setText(Html.fromHtml(directionAdapter.getHtml_instructions()));
//        list_direction_location.setText(directionAdapter.getLogin_id());
        list_direction_time_distance.setText(directionAdapter.getDistance()+" ("+directionAdapter.getDuration()+")");

        switch (directionAdapter.getManeuver()) {
            case "turn-sharp-left":
                image_direction.setImageResource(R.drawable.turn_sharp_left);
                break;
            case "turn-sharp-right":
                image_direction.setImageResource(R.drawable.turn_sharp_right);
                break;
            case "turn-slight-right":
                image_direction.setImageResource(R.drawable.turn_slight_right);
                break;
            case "turn-slight-left":
                image_direction.setImageResource(R.drawable.turn_slight_left);
                break;
            case "roundabout-left":
                image_direction.setImageResource(R.drawable.roundabout_left);
                break;
            case "roundabout-right":
                image_direction.setImageResource(R.drawable.roundabout_right);
                break;
            case "turn-left":
                image_direction.setImageResource(R.drawable.turn_left);
                break;
            case "turn-right":
                image_direction.setImageResource(R.drawable.turn_right);
                break;
            case "keep-left":
                image_direction.setImageResource(R.drawable.keep_left);
                break;
            case "keep-right":
                image_direction.setImageResource(R.drawable.keep_right);
                break;
            case "uturn-right":
                image_direction.setImageResource(R.drawable.uturn_right);
                break;
            case "uturn-left":
                image_direction.setImageResource(R.drawable.uturn_left);
                break;
            case "ramp-right":
                image_direction.setImageResource(R.drawable.ramp_right);
                break;
            case "ramp-left":
                image_direction.setImageResource(R.drawable.ramp_left);
                break;
            case "fork-right":
                image_direction.setImageResource(R.drawable.fork_right);
                break;
            case "fork-left":
                image_direction.setImageResource(R.drawable.fork_left);
                break;
            case "merge":
                image_direction.setImageResource(R.drawable.merge);
                break;
            case "straight":
                image_direction.setImageResource(R.drawable.straight);
                break;
            case "ferry-train":
                image_direction.setImageResource(R.drawable.ferry_train);
                break;
            case "ferry":
                image_direction.setImageResource(R.drawable.ferry);
                break;
        }

        return convertView;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }

}
