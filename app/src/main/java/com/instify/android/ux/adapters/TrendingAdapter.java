package com.instify.android.ux.adapters;

public class TrendingAdapter {
    public static final String[] data = {
            "Chemistry Surprise Test tomorrow from Chapter 3", "New Event Registrations open", "Material Science Chapter 5 notes uploaded", "Best of Luck for University Exams"
    };
}

/*public class AdapterTrending extends RecyclerView.Adapter<AdapterTrending.ViewHolder> {
    List<String> versionModels;
    //private RecyclerView.OnItemTouchListener onItemTouchListener;
    Context context;

    public AdapterTrending(Context context){
        this.context = context;
    }

    public AdapterTrending(List<String> versionModels){
        this.versionModels = versionModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_list, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(versionModels.get(position));
    }

    @Override
    public int getItemCount() {
        return versionModels == null ? 0 : versionModels.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardItemLayout;
        TextView title;

        public ViewHolder(View itemView){
            super(itemView);
            cardItemLayout = (CardView)itemView.findViewById(R.id.cardList_item);
            //cardItemLayout = (CardView)itemView.findViewById(R.id.imageItem);
            title = (TextView)itemView.findViewById(R.id.list_item_title);
        }
    }
}*/