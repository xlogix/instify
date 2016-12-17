package com.instify.android.ux.adapters;

/**
 * Created by Abhish3k on 3/8/2016.
 */

public class NotesAdapter {
    public static final String[] data = {
            "Advanced Calculus And Complex Analysis", "Basic Electrical Engineering", "Basic Mechanical Engineering",
            "Biology for Engineers", "Chemistry", "Program Design & Development using C", "Materials Science", "Programming Lab",
            "Value Added Courses"
    };
}
/*public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.ViewHolder> {

    List<String> versionModels;
    //private RecyclerView.OnItemTouchListener onItemTouchListener;
    Context context;

    public AdapterNotes(Context context){
        this.context = context;
    }

    public AdapterNotes(List<String> versionModels){
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
