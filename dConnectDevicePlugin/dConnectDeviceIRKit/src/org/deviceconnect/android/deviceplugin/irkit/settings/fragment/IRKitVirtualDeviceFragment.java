/*
 IRKitVirtualDeviceFragment.java
 Copyright (c) 2015 NTT DOCOMO,INC.
 Released under the MIT license
 http://opensource.org/licenses/mit-license.php
 */
package org.deviceconnect.android.deviceplugin.irkit.settings.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.deviceconnect.android.deviceplugin.irkit.R;
import org.deviceconnect.android.deviceplugin.irkit.data.IRKitDBHelper;
import org.deviceconnect.android.deviceplugin.irkit.data.VirtualDeviceData;
import org.deviceconnect.android.deviceplugin.irkit.settings.activity.IRKitDeviceListActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * IRKit Virtual Device List fragment.
 * 
 * @author NTT DOCOMO, INC.
 */
public class IRKitVirtualDeviceFragment extends Fragment
        implements IRKitCreateVirtualDeviceDialogFragment.IRKitVirtualDeviceCreateEventListener {

    /** Adapter. */
    private VirtualDeviceAdapter mVirtualDeviceAdapter;
    /** Devices. */
    private List<VirtualDeviceData> mVirtuals;
    /** サービスID. */
    private String mServiceId;
    /** DB Helper. */
    private IRKitDBHelper mDBHelper;
    /** 削除モードフラグ. */
    private boolean mIsRemoved;
    /** 削除フラグリスト. */
    private List<Boolean> mIsRemoves;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mIsRemoved = false;
        updateVirtualDeviceList();
    }

    /**
     * サービスID を受け取る.
     * @param serviceId サービスID
     */
    public void setServiceId(final String serviceId) {
        mServiceId = serviceId;
    }
    /**
     * IRKitのVirtual Device データを保持する。
     * @param device IRKitのVirtual Device データ
     * @return DeviceContainer
     */
    private VirtualDeviceContainer createContainer(final VirtualDeviceData device) {
        VirtualDeviceContainer container = new VirtualDeviceContainer();
        if (device.getCategoryName().equals("ライト")) {
            container.setIcon(getResources().getDrawable(R.drawable.light));
        } else {
            container.setIcon(getResources().getDrawable(R.drawable.tv));
        }
        container.setLabel(device.getDeviceName());
        container.setIsRemove(false);
        return container;
    }

    /**
     *　IRKitデバイスのリストの取得.
     * @return IRKitデバイスのリスト.
     */
    private List<VirtualDeviceContainer> createDeviceContainers() {
        List<VirtualDeviceContainer> containers = new ArrayList<VirtualDeviceContainer>();
        mVirtuals = mDBHelper.getVirtualDevices(null);
        if (mVirtuals != null) {
            for (int i = 0; i < mVirtuals.size(); i++) {
                mIsRemoves.add(false);
            }
            for (VirtualDeviceData device : mVirtuals) {
                containers.add(createContainer(device));
            }
        }
        return containers;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        final MenuItem menuItem = menu.add("CLOSE");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem item) {

                if (item.getTitle().equals(menuItem.getTitle())) {
                    getActivity().finish();
                }
                return true;
            }
        });
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        mIsRemoves = new ArrayList<Boolean>();

        IRKitCreateVirtualDeviceDialogFragment.newInstance().setEventListner(this);
        mDBHelper = new IRKitDBHelper(getActivity());
        mVirtualDeviceAdapter = new VirtualDeviceAdapter(getActivity(), createDeviceContainers());
        View rootView = inflater.inflate(R.layout.fragment_virtual_device_list, container, false);
        final Button leftBtn = (Button) rootView.findViewById(R.id.add_virtual_device);
        final TextView titleView = (TextView) rootView.findViewById(R.id.text_view_number);
        final Button rightBtn = (Button) rootView.findViewById(R.id.remove_virtual_device);
        leftBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mIsRemoved) {
                    mIsRemoved = false;
                    titleView.setBackgroundColor(Color.parseColor("#00a0e9"));
                    leftBtn.setBackgroundResource(R.drawable.button_blue);
                    leftBtn.setText("追加");
                    rightBtn.setBackgroundResource(R.drawable.button_blue);

                } else {
                    IRKitCategorySelectDialogFragment irkitDialog =
                            IRKitCategorySelectDialogFragment.newInstance();
                    irkitDialog.setServiceId(mServiceId);
                    irkitDialog.show(getActivity().getFragmentManager(),
                            "fragment_dialog");
                }
                updateVirtualDeviceList();
            }
        });
        rightBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mIsRemoved) {
                    if (isRemove()) {
                        removeCheckVirtualDevices();
                        mIsRemoved = false;
                        titleView.setBackgroundColor(Color.parseColor("#00a0e9"));
                        leftBtn.setBackgroundResource(R.drawable.button_blue);
                        leftBtn.setText("追加");
                        rightBtn.setBackgroundResource(R.drawable.button_blue);

                    } else {
                        IRKitCreateVirtualDeviceDialogFragment.showAlert(getActivity(),
                                "削除", "削除するデバイスを一つ選んでください。");
                    }
                } else {
                    mIsRemoved = true;
                    titleView.setBackgroundColor(Color.parseColor("#ffb6c1"));
                    leftBtn.setBackgroundResource(R.drawable.button_pink);
                    leftBtn.setText("キャンセル");
                    rightBtn.setBackgroundResource(R.drawable.button_pink);

                }
                updateVirtualDeviceList();
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.listview_devicelist);
        listView.setItemsCanFocus(true);
        listView.setAdapter(mVirtualDeviceAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
                IRKitDeviceListActivity activity = (IRKitDeviceListActivity) getActivity();
                activity.startApp(IRKitDeviceListActivity.MANAGE_VIRTUAL_PROFILE_PAGE,
                        mVirtuals.get(position).getServiceId());
            }
        });
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update device list.
     */
    public void updateVirtualDeviceList() {
        if (mVirtualDeviceAdapter == null) {
            return;
        }
        if (getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mVirtualDeviceAdapter.clear();
                mVirtualDeviceAdapter.addAll(createDeviceContainers());
                mVirtualDeviceAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onCreated() {
        updateVirtualDeviceList();
    }


    /**
     *  削除確認ダイアログを表示する.
     */
    private void showRemoveConfirmDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("title")
                .setMessage("message")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeCheckVirtualDevices();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }



    /**
     * Virtual Device を削除する.
     */
    private void removeCheckVirtualDevices() {
        boolean isRemoved = false;
        for (int i = 0; i < mIsRemoves.size(); i++) {
            if (mIsRemoves.get(i).booleanValue()) {
                isRemoved = mDBHelper.removeVirtualDevice(mVirtuals.get(i));
            }
        }
        if (isRemoved) {
            IRKitCreateVirtualDeviceDialogFragment.showAlert(getActivity(), "削除", "削除に成功しました。");
        } else {
            IRKitCreateVirtualDeviceDialogFragment.showAlert(getActivity(), "削除", "削除に失敗しました。");
        }
    }

    /**
     * チェックされているかどうかを確認する.
     */
    private boolean isRemove() {
        boolean isRemoved = false;
        for (int i = 0; i < mIsRemoves.size(); i++) {
            if (mIsRemoves.get(i).booleanValue()) {
                isRemoved = true;
                break;
            }
        }
        return isRemoved;
    }


    /**
     * VirtualDeviceContainer.
     */
    static class VirtualDeviceContainer {
        /** ラベル. */
        private String mLabel;
        /** アイコン. */
        private Drawable mIcon;
        /** チェック状態. */
        private boolean mIsRemove;

        /**
         * デバイスラベルの取得.
         * 
         * @return デバイスラベル.
         */
        public String getLabel() {
            return mLabel;
        }

        /**
         * デバイスラベルの設定.
         * 
         * @param label デバイスラベル.
         */
        public void setLabel(final String label) {
            if (label == null) {
                mLabel = "Unknown";
            } else {
                mLabel = label;
            }
        }
        /**
         * デバイスアイコンの取得.
         * @return デバイスアイコン
         */
        public Drawable getIcon() {
            return mIcon;
        }

        /**
         * デバイスアイコンの設定.
         * @param icon デバイスアイコン
         */
        public void setIcon(final Drawable icon) {
            mIcon = icon;
        }

        /**
         * デバイスを削除するかどうかを設定する.
         * @param isRemove true:削除, false: 削除しない
         */
        public void setIsRemove(final boolean isRemove) {
            mIsRemove = isRemove;
        }

        /**
         * デバイスを削除するかどうかを指定する.
         * @return 削除するかどうか
         */
        public boolean isRemove() {
            return mIsRemove;
        }

    }

    /**
     * VirtualDeviceAdapter.
     */
    private class VirtualDeviceAdapter extends ArrayAdapter<VirtualDeviceContainer> {
        /** LayoutInflater. */
        private LayoutInflater mInflater;

        /**
         * コンストラクタ.
         * 
         * @param context Context.
         * @param objects DeviceList.
         */
        public VirtualDeviceAdapter(final Context context, final List<VirtualDeviceContainer> objects) {
            super(context, 0, objects);
            mInflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent) {
            View cv = convertView;
            if (convertView == null) {
                cv = mInflater.inflate(R.layout.item_irkitdevice_list, parent, false);
            } else {
                cv = convertView;
            }

            final VirtualDeviceContainer device = getItem(position);

            String name = device.getLabel();

            TextView nameView = (TextView) cv.findViewById(R.id.devicelist_package_name);
            nameView.setText(name);
            Drawable icon = device.getIcon();
            if (icon != null) {
                ImageView iconView = (ImageView) cv.findViewById(R.id.devicelist_icon);
                iconView.setImageDrawable(icon);
            }

            CheckBox removeCheck = (CheckBox) cv.findViewById(R.id.delete_check);
            if (mIsRemoved) {
                removeCheck.setVisibility(View.VISIBLE);
                removeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        mIsRemoves.set(position, b);
                    }
                });
                removeCheck.setChecked(device.isRemove());
            } else {
                removeCheck.setVisibility(View.GONE);
                removeCheck.setOnCheckedChangeListener(null);
            }
            return cv;
        }
    }
}

