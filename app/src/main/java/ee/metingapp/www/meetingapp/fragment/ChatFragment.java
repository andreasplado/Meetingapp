package ee.metingapp.www.meetingapp.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.PushService;

import java.util.ArrayList;
import java.util.List;

import ee.metingapp.www.meetingapp.R;
import ee.metingapp.www.meetingapp.adapters.ChatListAdapter;
import ee.metingapp.www.meetingapp.data.Message;

/**
 * Created by Andreas on 10.12.2015.
 */
public class ChatFragment extends Fragment {

    private static String sUserId;
    private static final String TAG = ChatFragment.class.getName();
    public static final String USER_ID_KEY = "userId";
    private static final int MAX_CHAT_MESSAGES_TO_SHOW = 500;
    private static final int SCROLLABLE_HISTORY_BATCH_SIZE = 10;
    private EditText etMessage;
    private Button btSend;
    private View view;
    private ListView lvChat;
    private ArrayList<Message> mMessages;
    private ChatListAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    private boolean mFirstLoad;
    private Handler handler = new Handler();
    private List<Message> messagesFromServer;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_chat,
                container, false);

        if (ParseUser.getCurrentUser() != null) { // start with existing user
            startWithCurrentUser();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
        // Run the runnable object defined every 100ms
        handler.postDelayed(runnable, 100);
        return view;
    }

    // Get the userId from the cached currentUser object
    private void startWithCurrentUser() {
        sUserId = ParseUser.getCurrentUser().getObjectId();
        setupMessagePosting();
    }

    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    private void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.d(TAG, "Anonymous login failed: " + e.toString());
                } else {
                    startWithCurrentUser();
                }
            }
        });
    }

    // Setup button event handler which posts the entered message to Parse
    // Setup message field and posting
    private void setupMessagePosting() {
        etMessage = (EditText) view.findViewById(R.id.etMessage);
        btSend = (Button) view.findViewById(R.id.btSend);
        lvChat = (ListView) view.findViewById(R.id.lvChat);

        mMessages = new ArrayList<Message>();
        messagesFromServer = new ArrayList<>();
        // Automatically scroll to the bottom when a data set change notification is received and only if the last item is already visible on screen. Don't scroll to the bottom otherwise.
        lvChat.setTranscriptMode(1);
        mFirstLoad = true;
        mAdapter = new ChatListAdapter(getActivity(), sUserId, mMessages);
        lvChat.setAdapter(mAdapter);
        btSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String body = etMessage.getText().toString();
                // Use Message model to create new messages now
                Message message = new Message();
                message.setUserId(sUserId);
                message.setBody(body);
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        receiveMessage();
                    }
                });
                etMessage.setText("");
            }
        });

        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                final ListView lw = lvChat;

                if (scrollState == 0)
                    Log.i("meetingapp", "scrolling stopped...");


                if (view.getId() == lw.getId()) {
                    final int currentFirstVisibleItem = lw.getFirstVisiblePosition();
                    Log.d("meetingapp", String.valueOf(currentFirstVisibleItem));
                    if (currentFirstVisibleItem == 0) {
                        /*if (messagesFromServer.size() > SCROLLABLE_HISTORY_BATCH_SIZE * 2) {
                            for (int i = 0; i < SCROLLABLE_HISTORY_BATCH_SIZE; ++i) {
                                mMessages.add(messagesFromServer.get(messagesFromServer.size() - SCROLLABLE_HISTORY_BATCH_SIZE * 2 + i));
                            }
                        } else {
                            mMessages.addAll(messagesFromServer);
                        }
                        Toast.makeText(getContext(), String.valueOf(mMessages.size()), Toast.LENGTH_LONG).show();
                        mAdapter.notifyDataSetChanged();*/
                    }
                }
            }
        });
    }

    private void receiveMessage() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);
        query.orderByAscending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                   /* messagesFromServer.addAll(messages);
                    mMessages.clear();
                    if (messages.size() > SCROLLABLE_HISTORY_BATCH_SIZE) {
                        for (int i = 0; i < SCROLLABLE_HISTORY_BATCH_SIZE; ++i) {
                            mMessages.add(messages.get(messages.size() - SCROLLABLE_HISTORY_BATCH_SIZE + i));
                        }
                    } else {
                        mMessages.addAll(messages);
                    }
                    Log.d("meetingapp", "receiveMessage");
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        lvChat.setSelection(mAdapter.getCount() - 1);
                        mFirstLoad = false;
                    }*/
                } else {
                    Log.d("message", "Error: " + e.getMessage());
                }
            }
        });
    }

    // Defines a runnable which is run every 100ms
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            handler.postDelayed(this, 100);
        }
    };

    private void refreshMessages() {
        receiveMessage();
    }
}
