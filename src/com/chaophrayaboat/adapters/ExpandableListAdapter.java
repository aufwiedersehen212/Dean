package com.chaophrayaboat.adapters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.chaophrayaboat.R;
import com.chaophrayaboat.models.Quay;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<Quay> quaysList;
	private List<Quay> origQuaysList;
	private ExpandableListView listView;
	private int lastExpandedGroupPosition;
	public enum Mode {
		TRANSPORTATION, PLACE
	}
	private Mode mode;

	public ExpandableListAdapter(Context context, List<String> listDataHeader,
			HashMap<String, List<String>> listChildData) {
		this._context = context;
	}

	public ExpandableListAdapter(Context context, ExpandableListView listView, List<Quay> quaysList, Mode mode) {
		this._context = context;
		this.listView = listView;
		this.quaysList = quaysList;
		this.origQuaysList = new ArrayList<Quay>(quaysList);
		this.mode = mode;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		switch (this.mode) {
			case TRANSPORTATION :
				return this.quaysList.get(groupPosition).otherTransportations.get(childPosititon);
			case PLACE :
				return this.quaysList.get(groupPosition).nearbyPlaces.get(childPosititon);
			default :
				return this.quaysList.get(groupPosition).otherTransportations.get(childPosititon);
		}
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		final String childText = (String) getChild(groupPosition, childPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			switch (this.mode) {
				case TRANSPORTATION :
					convertView = infalInflater.inflate(R.layout.list_item, null);
					break;
				case PLACE :
					convertView = infalInflater.inflate(R.layout.place_list_item, null);
					break;
			}
		}
		TextView txtListChild = (TextView) convertView.findViewById(R.id.child_item);
		txtListChild.setText(childText);
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		switch (this.mode) {
			case TRANSPORTATION :
				return this.quaysList.get(groupPosition).otherTransportations.size();
			case PLACE :
				return this.quaysList.get(groupPosition).nearbyPlaces.size();
			default :
				return this.quaysList.get(groupPosition).otherTransportations.size();
		}
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this.quaysList.get(groupPosition);
	}
	@Override
	public int getGroupCount() {
		return this.quaysList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		Quay quay = (Quay) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.list_group, null);
		}

		TextView nameEn = (TextView) convertView.findViewById(R.id.name_en);
		nameEn.setTypeface(null, Typeface.BOLD);
		nameEn.setText(quay.nameEn);

		TextView nameTh = (TextView) convertView.findViewById(R.id.name_th);
		nameTh.setText(quay.nameTh);

		return convertView;
	}

	@Override
	public void onGroupExpanded(int groupPosition) {
		if (groupPosition != lastExpandedGroupPosition) {
			listView.collapseGroup(lastExpandedGroupPosition);
			listView.setSelection(groupPosition);
		}
		super.onGroupExpanded(groupPosition);
		lastExpandedGroupPosition = groupPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	@SuppressLint("DefaultLocale")
	public void filterData(String query) {
		query = query.toLowerCase();
		quaysList.clear();

		if (query.isEmpty()) {
			quaysList.addAll(origQuaysList);
		} else {

			for (Quay quay : origQuaysList) {

				List<String> transportations = quay.otherTransportations;
				List<String> newList = new ArrayList<String>();
				for (String transportation : transportations) {
					if (transportation.toLowerCase().contains(query)) {
						newList.add(transportation);
					}
				}
				if (newList.size() > 0) {
					Quay newQuay = new Quay();
					newQuay.otherTransportations = transportations;
					quaysList.add(newQuay);
				}
			}
		}
		notifyDataSetChanged();
	}
}
