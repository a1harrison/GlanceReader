package pro.dbro.glance.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import java.util.Calendar;
import java.util.Date;

import pro.dbro.glance.R;
import pro.dbro.glance.activities.MainActivity;

public class ArticleAdapter extends ParseQueryAdapter<ParseObject> {

    public static enum ArticleFilter {
        RECENT,
        ALL
    }

    public ArticleAdapter(Context context) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery query = new ParseQuery("Article");
                return query;
            }
        });
    }

    public ArticleAdapter(final Context context, final ArticleFilter filterType) {
        super(context, new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery<ParseObject> create() {

                        ParseQuery query = null;
                        switch(filterType) {
                            case ALL:
                                query = new ParseQuery("Article");
                                query.orderByDescending("createdAt");
                                return query;
                            case RECENT:
                            default:
                                query = new ParseQuery("Article");
                                Date now = new Date();
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(now);
                                cal.add(Calendar.DAY_OF_MONTH, -2);
                                Date oneDayAgo = cal.getTime();
                                query.whereGreaterThan("createdAt", oneDayAgo);
                                query.orderByDescending("reads");
                                return query;
                        }
                    }
            });
    }

    @Override
    public View getItemView(ParseObject object, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(getContext(), R.layout.article_list_item, null);
        }

        TextView handle = (TextView) convertView.findViewById(R.id.title);
        handle.setText(object.getString("title"));

        TextView text = (TextView) convertView.findViewById(R.id.url);
        text.setText(object.getString("url"));

        TextView reads = (TextView) convertView.findViewById(R.id.reads);
        reads.setText(new Integer(object.getInt("reads")).toString() + " glances");
        reads.setLines(1);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView tv = (TextView) view.findViewById(R.id.url);
                Intent communityIntent = new Intent(getContext(), MainActivity.class);
                communityIntent.setAction(Intent.ACTION_SEND);
                communityIntent.putExtra(Intent.EXTRA_TEXT, tv.getText());
                getContext().startActivity(communityIntent);
            }
        });

        return convertView;
    }
}