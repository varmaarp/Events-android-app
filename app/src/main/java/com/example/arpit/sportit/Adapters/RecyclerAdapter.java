package com.example.arpit.sportit.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.R;

import java.util.ArrayList;

/**
 * Created by Arpit on 12-05-2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Event> events;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item, parent, false);

        return new ViewHolder(listItemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Event e = events.get(position);
        holder.eventName.setText(e.getEventName());
        holder.eventDateTime.setText(e.getDate() + ", " + e.getTime());
        holder.eventPlace.setText(e.getPlace());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eventName;
        private final TextView eventDateTime;
        private final TextView eventPlace;

        public ViewHolder(View itemView) {
            super(itemView);
            eventName = (TextView) itemView.findViewById(R.id.event_name);
            eventDateTime = (TextView) itemView.findViewById(R.id.event_date);
            eventPlace = (TextView) itemView.findViewById(R.id.event_place);
        }
    }

    public RecyclerAdapter(ArrayList<Event> events){
        this.events = events;
    }
}
