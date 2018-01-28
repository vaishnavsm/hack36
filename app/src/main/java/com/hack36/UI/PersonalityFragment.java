package com.hack36.UI;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hack36.Models.BigFiveTraits;
import com.hack36.Models.Personality;
import com.hack36.Models.Trait;
import com.hack36.R;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.LineChartView;
import lecho.lib.hellocharts.view.PieChartView;

import static com.hack36.Utils.Utils.myLog;

public class PersonalityFragment extends Fragment{
//    @BindView(R.id.generic_list_view) ListView listView;
    @BindView(R.id.chart) PieChartView pieChartView;

    ProgressDialog dialog;

    public PersonalityFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_details, container, false);
        ButterKnife.bind(this,rootView);

        initUI();
        return rootView;
    }

    private void initUI() {

        dialog = new ProgressDialog(getContext());
        dialog.setMessage("Fetching Insights");
        dialog.setCancelable(false);
        dialog.show();

        final String text = "One day, my friend and colleague (a black woman) and I were sitting in a room with two white women while our clients were in groups. The supervisor stopped by and greeted the white women before returning to his office, as if my colleague and I were not even present. I later realized that I was not on the supervisor’s list for team emails, and I was missing important updates on my clients. I sent an email to request to be added to the list, our first correspondence ever, occurring nearly two months after me joining the unit.\n" +
                "\n" +
                "So, I decided to test things: I made up my mind that I would see the supervisor in passing and greet him loudly so there was no doubt he could hear me. I did this, and called him by name with a smile as I passed. He still ignored me. As he continued to walk, he spoke to a white woman who was walking in the same direction as me. At that point, I realized his behavior was not simply a figment of my imagination. Rather, it had to be racism.\n" +
                "\n" +
                "I pride myself on my assertiveness in school and the workplace, and I took comfort in the transparency of the supervisory relationship with my clinical supervisor. During one of our sessions, I decided to confide in him about the way that things had transpired between the unit supervisor and me. I shared the aforementioned examples. As I spoke, my supervisor seemed uncomfortable. So, I proceeded cautiously with my next statement: “I’m not calling him racist, but the way I have been treated feels like racism.” My supervisor responded hesitantly and noted that he had heard another person mention the “microaggressions” from other staff on the unit. As he continued to skirt around the issue, I felt frustrated with sharing my experience because he invalidated it by using a term to, essentially, soften the blow -- and he did not offer a course for resolution; instead, I offered my own.\n" +
                "\n" +
                "When therapy is concerned, I always say, “It is not the responsibility of the client to educate the therapist.” I feel similarly when issues concerning various -isms are involved: it is not the responsibility of the oppressed to educate the oppressors. Yet that often ends up being the case.\n" +
                "\n" +
                "Even in professional and academic spheres, the narratives of the oppressed are frequently excluded and replaced with generic (read: privileged) accounts, placing the onus on marginalized people to inform the privileged about their experiences. Similarly, when the narratives of the oppressed are included, there are many instances wherein the true struggle of oppression is glossed over in efforts to protect the feelings of those who may not empathize with the oppressed group. In a place where I assumed that my concerns would be validated and that my supervisor would advocate for and with me, I was disappointed by his passivity and efforts to sugarcoat what was obviously racism. I had colleagues who validated my experience and shared their own, but it is extremely difficult to change things for the better without those in positions of power on your side.\n" +
                "\n" +
                "As I reflect on the courses I was taking during that time, none of them seemed especially appropriate for me to share these experiences of racist behavior. Many colleges and universities pride themselves on their commitment to diversity, yet that commitment often seems to be superficial. When course work focuses on specific examples that regularly showcase white, heterosexual, able-bodied individuals, the narratives of people of color, people who are disabled, LGBTQ people and so many more are dismissed and “othered” in the process.";

        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),text);

        final Request request = new Request.Builder()
                .url("https://gateway.watsonplatform.net/personality-insights/api/v3/profile?version=2017-10-13")
                .post(requestBody)
                .addHeader("Content-Type", "text/plain")
                .addHeader("Authorization", "Basic MjMwMzA1OWEtZjgxZi00M2NmLWIxYTctNjk1MDRiZTBlNGFmOmVSa0F2bTVjM25tZg==")
                .addHeader("Cache-Control", "no-cache")
                .build();


        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                myLog("Request Failed",request.body());
                dialog.dismiss();
            }

            @Override
            public void onResponse(Response response) throws IOException {
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<Personality> jsonAdapter = moshi.adapter(Personality.class);

                Personality personality = jsonAdapter.fromJson(response.body().string());
                if (personality != null){
                    List<String> traits = new ArrayList<>();

                    traits.add("Values");
                    for (Trait t: personality.getValues())
                        traits.add(t.getName()+"-"+t.getPercentile());

                    traits.add("Needs");
                    for (Trait t: personality.getNeeds())
                        traits.add(t.getName()+"-"+t.getPercentile());

                    traits.add("Persona");
                    for (BigFiveTraits b: personality.getPersonality())
                        traits.add(b.getName()+"-"+b.getPercentile());

                    updateList(traits);
                }

                dialog.dismiss();
            }
        });
    }

    // I don't know why it fails
    // Due to some threading
    void updateList(final List<String> traits){
        Handler mainHandler = new Handler(getContext().getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {

                pieChartView.setInteractive(true);
                pieChartView.setZoomEnabled(true);
                pieChartView.setZoomType(ZoomType.HORIZONTAL_AND_VERTICAL);

                PieChartData data = new PieChartData();
                List<SliceValue> values = new ArrayList<>();
                values.add(new SliceValue(traits.indexOf("Values")));
                values.add(new SliceValue(traits.indexOf("Needs") - traits.indexOf("Values")));
                values.add(new SliceValue(traits.indexOf("Persona") - traits.indexOf("Needs")));
                values.add(new SliceValue(traits.size() - traits.indexOf("Persona")));

                data.setValues(values);
                pieChartView.setPieChartData(data);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
//                        android.R.layout.simple_list_item_1, android.R.id.text1, traits);
//                listView.setAdapter(adapter);
//                adapter.notifyDataSetChanged();
            }
        });

    }
}
