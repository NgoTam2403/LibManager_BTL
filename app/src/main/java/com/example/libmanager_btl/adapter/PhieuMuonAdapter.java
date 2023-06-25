package com.example.libmanager_btl.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.libmanager_btl.OnItemClickListener;
import com.example.libmanager_btl.R;
import com.example.libmanager_btl.dao.SachDAO;
import com.example.libmanager_btl.dao.ThanhVienDAO;
import com.example.libmanager_btl.fragment.PhieuMuonFragment;
import com.example.libmanager_btl.model.PhieuMuon;
import com.example.libmanager_btl.model.Sach;
import com.example.libmanager_btl.model.ThanhVien;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class PhieuMuonAdapter extends RecyclerView.Adapter<PhieuMuonAdapter.SpendingHolder> {

    private Context context;
    private PhieuMuonFragment fragment;
    private List<PhieuMuon> list;
    private SachDAO sachDAO;
    private ThanhVienDAO thanhVienDAO;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    private OnItemClickListener itemClickListener;

    private List<PhieuMuon> filteredList; // Danh sách mục đã lọc
    private List<PhieuMuon> originalList; // Danh sách gốc

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            filteredList.clear(); // Clear the previously filtered list

            if (originalList == null) {
                originalList = new ArrayList<>(list); // Copy the original list
            }

            if (constraint == null || constraint.length() == 0) {
                // If there is no search constraint, return the entire original list
                filteredList.addAll(originalList);
            } else {
                // Filter the original list based on the search constraint
                String filterPattern = constraint.toString().trim();
                for (PhieuMuon phieuMuon : originalList) {
                    String maPM = String.valueOf(phieuMuon.getMaPM());
                    if (maPM.contains(filterPattern)) {
                        filteredList.add(phieuMuon);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list.clear();
            if (results != null && results.count > 0) {
                list.addAll((List<PhieuMuon>) results.values);
            } else {
                list.addAll(originalList);
            }
            notifyDataSetChanged();
        }
    };

    public PhieuMuonAdapter(Context context, PhieuMuonFragment fragment) {
        this.context = context;
        this.fragment = fragment;
        this.list = new ArrayList<>();
        sachDAO = new SachDAO(context);
        thanhVienDAO = new ThanhVienDAO(context);
        filteredList = new ArrayList<>();
    }

    @NonNull
    @Override
    public SpendingHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phieu_muon, parent, false);
        return new SpendingHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SpendingHolder holder, int position) {
        if (list != null) {
            PhieuMuon phieuMuon = list.get(position);

            // set data to view
            holder.tvMaPM.setText("Mã phiếu: " + phieuMuon.getMaPM());
            Sach sach = sachDAO.getId(phieuMuon.getMaSach() + "");
            holder.tvTenSach.setText("Tên sách: " + sach.getTenSach());
            ThanhVien thanhVien = thanhVienDAO.getWithID(phieuMuon.getMaTV() + "");
            holder.tvTenTV.setText("Thành viên: " + thanhVien.getHoTen());
            holder.tvTienThue.setText("Tiền thuê: " + phieuMuon.getTienThue());
            holder.tvNgay.setText(String.format("Ngày: " + sdf.format(phieuMuon.getNgay())));
            holder.tvSoLuong.setText("Số lượng: " + phieuMuon.getSoLuong());

            if (phieuMuon.getTraSach() == PhieuMuon.DA_TRA) {
                holder.tvTraSach.setTextColor(Color.GREEN);
                holder.tvTraSach.setText("Đã trả sách");
            } else {
                holder.tvTraSach.setTextColor(Color.RED);
                holder.tvTraSach.setText("Chưa trả sách");
            }

            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Xử lý sự kiện click nút xóa
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            itemClickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list != null) return list.size();
        return 0;
    }

    public void setData(List<PhieuMuon> list){
        this.list = list;
        this.originalList = new ArrayList<>(list);
        notifyDataSetChanged();
    }

    public Filter getFilter() {
        return filter;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public class SpendingHolder extends RecyclerView.ViewHolder {
        private TextView tvMaPM, tvTenTV, tvTienThue, tvTenSach, tvNgay, tvTraSach, tvSoLuong;
        private ImageView imgDelete;

        public SpendingHolder(@NonNull View itemView) {
            super(itemView);
            tvMaPM = itemView.findViewById(R.id.tvMaPM);
            tvTenTV = itemView.findViewById(R.id.tvTenTV);
            tvTienThue = itemView.findViewById(R.id.tvTienThue);
            tvTenSach = itemView.findViewById(R.id.tvTenSach);
            tvNgay = itemView.findViewById(R.id.tvNgayMuon);
            tvTraSach = itemView.findViewById(R.id.tvTraSach);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
            imgDelete = itemView.findViewById(R.id.imgDeletePM);
        }
    }
}
