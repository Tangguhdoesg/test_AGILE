package com.example.flourish.MainFragment.TodoList;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.flourish.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TodoListRecycler extends Fragment{

    private DatabaseReference ref;
    private FirebaseAuth mAuth;

    private TextView todoNoData;

    private FirebaseRecyclerOptions<ToDoList> options;
    private FirebaseRecyclerAdapter<ToDoList, TodoListHolder> adapter;

    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_list_recycler, container, false);

        mAuth = FirebaseAuth.getInstance();
        ref = FirebaseDatabase.getInstance().getReference().child("ToDoList").child(mAuth.getCurrentUser().getUid());

        recyclerView = rootView.findViewById(R.id.todoRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        todoNoData = rootView.findViewById(R.id.todoNoData);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) todoNoData.setVisibility(View.VISIBLE);
                else todoNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getChildrenCount() == 0) todoNoData.setVisibility(View.VISIBLE);
                else todoNoData.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        options = new FirebaseRecyclerOptions.Builder<ToDoList>().setQuery(ref, ToDoList.class).build();
        adapter = new FirebaseRecyclerAdapter<ToDoList, TodoListHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TodoListHolder holder, int position, @NonNull ToDoList model) {
                holder.title.setText(model.getTitle());
                holder.rightArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), EditTodo.class);
                        intent.putExtra("Todo", model);
                        startActivity(intent);
                    }
                });
                holder.checkBox.setChecked(model.getChecked());
                holder.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String path = model.getTitle().replace(" ", "_") + "_" + model.getDay() + "_" + model.getMonth() + "";
                        ref.child(path).child("checked").setValue(holder.checkBox.isChecked());
                    }
                });
            }

            @NonNull
            @Override
            public TodoListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_card, parent, false);
                return new TodoListHolder(view);
            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();

        return rootView;
    }

}