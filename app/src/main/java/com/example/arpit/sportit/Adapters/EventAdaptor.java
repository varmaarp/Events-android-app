package com.example.arpit.sportit.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;


import com.example.arpit.sportit.DataClasses.Event;
import com.example.arpit.sportit.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.R.attr.filter;

/**
 * Created by Arpit on 09-05-2017.
 */

public class EventAdaptor extends ArrayAdapter<Event> {

    private ArrayList<Event> filteredData;
    private ArrayList<Event> originalData;

    static class ViewHolderItem{
        private TextView vEventName;
        private TextView vEventPlace;
        private TextView vEventDate;
    }

    public EventAdaptor(Context context, ArrayList<Event> events){
        super(context, 0, events);
        filteredData = events;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        ViewHolderItem holder;

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
            holder = new ViewHolderItem();
            holder.vEventName = (TextView) convertView.findViewById(R.id.event_name);
            holder.vEventPlace = (TextView) convertView.findViewById(R.id.event_place);
            holder.vEventDate = (TextView) convertView.findViewById(R.id.event_date);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolderItem) convertView.getTag();
        }

        Event currentEvent = getItem(position);

        if (currentEvent.getIsCancelled()) {
            holder.vEventName.setText(currentEvent.getEventName() + " - CANCELLED");
        }else{
            holder.vEventName.setText(currentEvent.getEventName());
        }
        if (currentEvent.getPlace().indexOf('|') != -1) {
            holder.vEventPlace.setText(currentEvent.getPlace().substring(0, currentEvent.getPlace().indexOf('|')));
        }else{
            holder.vEventPlace.setText(currentEvent.getPlace());
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date local = null;
        try {
            Date convertedDate = sdf.parse(currentEvent.getDateTime());
            String timeZoneID = Calendar.getInstance().getTimeZone().getID();
            local = new Date(convertedDate.getTime() + TimeZone.getTimeZone(timeZoneID).getOffset(convertedDate.getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf = new SimpleDateFormat("dd MMM yyyy, h:mm a");
        String dateTime = sdf.format(local);
        holder.vEventDate.setText(dateTime);

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence prefix) {
                final FilterResults results = new FilterResults();

                if (originalData == null) {
                        originalData = new ArrayList<>(filteredData);
                }

                if (prefix == null || prefix.length() == 0) {
                    final ArrayList<Event> list = new ArrayList<>(originalData);

                    results.values = list;
                    results.count = list.size();
                } else {
                    final String prefixString = prefix.toString().toLowerCase();

                    final ArrayList<Event> values = new ArrayList<>(originalData);

                    final ArrayList<Event> newValues = new ArrayList<>();

                    for (Event e : values){
                        if (e.getEventName().toLowerCase().contains(prefixString)) {
                            newValues.add(e);
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                Log.v("results","results published " + results.count);
                filteredData = (ArrayList<Event>) results.values;
                notifyDataSetChanged();
                clear();
                addAll(filteredData);
                notifyDataSetInvalidated();

            }
        };
    }

}
