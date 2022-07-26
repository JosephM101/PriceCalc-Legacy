package com.josephm101.pricecalc;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.josephm101.pricecalc.ContextTattletale;
import com.josephm101.pricecalc.ListFileHandler.Common.ListInstance;
import com.josephm101.pricecalc.ListFileHandler.Json.ListFileHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

public class OpenList_Activity extends AppCompatActivity {
    final ActivityResultLauncher<Intent> MainActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
            });

    final ActivityResultLauncher<Intent> CreateNewListActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = null;
                    if (data != null) {
                        extras = data.getExtras();
                    }
                    if (extras != null) {
                        try {
                            UUID newUuid = ListFileHandler.CreateNewList(extras.getString("text"));
                            Submit(newUuid);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

    final ActivityResultLauncher<Intent> RenameListActivityLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    Bundle extras = null;
                    if (data != null) {
                        extras = data.getExtras();
                    }
                    if (extras != null) {
                        try {
                            UUID newUuid = ListFileHandler.CreateNewList(extras.getString("text"));
                            Submit(newUuid);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

    private static OpenList_CustomAdapter listAdapter;
    ArrayList<ListInstance> lists = new ArrayList<>();
    ListView listView;

    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_list);
        ThemeHandling.ApplyTheme(this.getApplicationContext()); // Apply theme
        setTitle("Open list");
        setFinishOnTouchOutside(false);
        Context c = this; // Save context

        Button button_newList = findViewById(R.id.button_createNewList);
        button_newList.setOnClickListener(view -> {
            Intent createNewList_Name_TextPrompt = new Intent(c, TextPromptDialog.class);
            createNewList_Name_TextPrompt.putExtra("title", "Create list");
            createNewList_Name_TextPrompt.putExtra("hint", "Enter list name");
            CreateNewListActivityLauncher.launch(createNewList_Name_TextPrompt);
        });

        listView = findViewById(R.id.UserLists);
        listAdapter = new OpenList_CustomAdapter(lists, OpenList_Activity.this);
        listAdapter.setNotifyOnChange(true);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            pos = i; // Get index
            ListInstance instance = lists.get(pos); // Get instance
            Submit(instance.getUuid()); // Submit with UUID
        });
        listView.setOnItemLongClickListener((adapterView, view, i, l) -> {
            pos = i; // Get index
            ListInstance instance = lists.get(pos);
            new MaterialAlertDialogBuilder(c, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                    .setMessage("What do you want to do?")
                    .setTitle("\"" + instance.getListFriendlyName() + "\"")
                    // .setPositiveButton("Rename", (dialog, which) -> {
                    //     /// TODO: Add code to rename list
                    // })
                    .setNegativeButton("Delete", (dialog, which) -> {
                        new MaterialAlertDialogBuilder(c, R.style.CustomTheme_MaterialComponents_MaterialAlertDialog)
                                .setMessage("You are about to delete the following list \r\n\r\n" + instance.getListFriendlyName() +
                                        " \r\n\r\nAre you sure you want to permanently delete this list? You cannot undo this action!")
                                .setTitle("Delete List?")
                                .setIcon(R.drawable.ic_baseline_delete_forever_24)
                                .setPositiveButton("Yes, delete it.", (_dialog, _which) -> {
                                    ListFileHandler.ResolveJsonFilename(instance.getUuid()).delete();
                                    lists.remove(instance);
                                    listAdapter.notifyDataSetChanged();
                                })
                                .setNegativeButton("No, don't!", (_dialog, _which) -> dialog.dismiss())
                                .setCancelable(true)
                                .show();
                    })
                    // .setNeutralButton("Dismiss", (dialog, which) -> dialog.dismiss())
                    .setNeutralButton("Open", (dialog, which) -> Submit(instance.getUuid()))
                    .setCancelable(true)
                    .show();
            return true;
        });

        // ContextTattletale.setLocalContext(getApplicationContext()); // Store the application context; MUST be done before ListFileHandler is initialized.
        // ListFileHandler.init(); // Tell ListFileHandler to initialize
        Update();
    }

    void Submit(UUID uuid) {
        Intent data = new Intent();
        data.putExtra("uuid", uuid.toString());
        setResult(RESULT_OK, data);
        finish();
    }

    void Update() {
        lists.clear();
        ArrayList<UUID> list_uuids = ListFileHandler.GetListUUIDs();
        for (UUID uuid : list_uuids) {
            try {
                lists.add(ListFileHandler.getListInstanceFromUUID(uuid));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        listAdapter.notifyDataSetChanged();
    }
}