package com.example.googlemap.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.googlemap.Guide.GuideModel;
import com.example.googlemap.Guide.GuideViewAdapter;
import com.example.googlemap.Pages.GuideActivity;
import com.example.googlemap.R;
import java.util.ArrayList;
import java.util.List;


public class EmergencyFragment extends Fragment {

    private RecyclerView recyclerView;
    private GuideViewAdapter adapter;

    private ViewPager2 viewPager2;

    public EmergencyFragment(ViewPager2 viewPager2) {
        this.viewPager2 = viewPager2;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.emergency_page_fragment, container, false);

        recyclerView = view.findViewById(R.id.recycleguide);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        List<GuideModel> itemList = createItemList();
        List <String> textlist = createList();
        adapter = new GuideViewAdapter(itemList, new GuideViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(GuideModel guidemodel) {

                if(guidemodel.getText().toString().equals("Earthquakes")){
                    Intent intent = new Intent(getActivity(), GuideActivity.class);
                    intent.putExtra("title", guidemodel.getText().toString());
                    intent.putExtra("text", textlist.get(0));
                    intent.putExtra("image",R.drawable.earthquake);
                    startActivity(intent);
                }

                if(guidemodel.getText().toString().equals("Floods")) {
                    Intent intent = new Intent(getActivity(), GuideActivity.class);
                    intent.putExtra("title", guidemodel.getText().toString());
                    intent.putExtra("text", textlist.get(1));
                    intent.putExtra("image",R.drawable.flood);
                    startActivity(intent);
                }

                if (guidemodel.getText().toString().equals("Wildfires")) {
                    Intent intent = new Intent(getActivity(), GuideActivity.class);
                    intent.putExtra("title", guidemodel.getText().toString());
                    intent.putExtra("text", textlist.get(2));
                    intent.putExtra("image",R.drawable.wildfire);
                    startActivity(intent);
                }

                if (guidemodel.getText().toString().equals("Hurricanes/Cyclones")) {
                    Intent intent = new Intent(getActivity(), GuideActivity.class);
                    intent.putExtra("title", guidemodel.getText().toString());
                    intent.putExtra("text", textlist.get(3));
                    intent.putExtra("image",R.drawable.cyclone);
                    startActivity(intent);
                }

                if (guidemodel.getText().toString().equals("Crime Safety Guide")) {
                    Intent intent = new Intent(getActivity(), GuideActivity.class);
                    intent.putExtra("title", guidemodel.getText().toString());
                    intent.putExtra("text", textlist.get(4));
                    intent.putExtra("image",R.drawable.safety);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<GuideModel> createItemList() {
        List<GuideModel> itemList = new ArrayList<>();
        itemList.add(new GuideModel("Earthquakes"));
        itemList.add(new GuideModel("Floods"));
        itemList.add(new GuideModel("Wildfires"));
        itemList.add(new GuideModel("Hurricanes/Cyclones"));
        itemList.add(new GuideModel("Crime Safety Guide"));
        // Add more items as needed
        return itemList;
    }

    private List<String> createList() {
        List<String> itemList = new ArrayList<>();
        itemList.add("Before:\n\n" +
                "\t- Secure heavy furniture and appliances.\n" +
                "\t- Create an emergency kit with essentials.\n" +
                "\t- Develop an evacuation plan with meeting points.\n" +
                "\nDuring:\n\n" +
                "\t- Drop, cover, and hold on.\n" +
                "\t- Stay indoors away from windows.\n" +
                "\t- If outside, move to an open area away from buildings.\n" +
                "\nAfter:\n\n" +
                "\t- Check for injuries and administer first aid.\n" +
                "\t- Be cautious of aftershocks.\n" +
                "\t- Follow emergency communication for updates.\n");
        itemList.add("Before:\n\n" +
                "\t- Know the flood risk in your area.\n" +
                "\t- Elevate utilities and appliances.\n" +
                "\t- Create an emergency evacuation plan.\n" +
                "\nDuring:\n\n" +
                "\t- Evacuate to higher ground if instructed.\n" +
                "\t- Avoid walking or driving through floodwaters.\n" +
                "\t- Listen to emergency broadcasts for updates.\n" +
                "\nAfter:\n\n" +
                "\t- Return home only when authorities say it's safe.\n" +
                "\t- Check for structural damage before entering.\n" +
                "\t- Discard contaminated items.\n");
        itemList.add("Before:\n\n" +
                "\t- Create a defensible space around your home.\n" +
                "\t- Have an emergency kit and evacuation plan.\n" +
                "\t- Monitor local fire alerts.\n" +
                "\nDuring:\n\n" +
                "\t- Evacuate early if instructed.\n" +
                "\t- Close windows and doors to prevent embers.\n" +
                "\t- Stay informed through emergency channels.\n" +
                "\nAfter:\n\n" +
                "\t- Wait for authorities to declare it safe to return.\n" +
                "\t- Be cautious of hotspots and flare-ups.\n" +
                "\t- Check for property damage.\n");
        itemList.add("Before:\n\n" +
                "\t- Board up windows and secure outdoor items.\n" +
                "\t- Have a hurricane evacuation plan.\n" +
                "\t- Keep emergency supplies on hand.\n" +
                "\nDuring:\n\n" +
                "\t- Stay indoors away from windows.\n" +
                "\t- Listen to weather updates.\n" +
                "\t- Evacuate if instructed by authorities.\n" +
                "\nAfter:\n\n" +
                "\t- Wait for the all-clear before returning.\n" +
                "\t- Beware of downed power lines and flooded areas.\n" +
                "\t- Check for structural damage.\n");
        itemList.add("1. Home Security:\n\n" +
                "\t- Install quality locks and deadbolts.\n" +
                "\t- Use timers for lights to give the appearance of someone being home.\n" +
                "\t- Keep doors and windows locked, even when at home.\n" +
                "\t- Install a peephole in the front door.\n" +
                "\n2. Personal Safety:\n\n" +
                "\t- Be aware of your surroundings.\n" +
                "\t- Avoid poorly lit or secluded areas.\n" +
                "\t- Trust your instincts; if something feels off, take precautions.\n" +
                "\t- Carry a personal alarm or whistle.\n" +
                "\n3. Vehicle Safety:\n\n" +
                "\t- Lock car doors and roll up windows when parked.\n" +
                "\t- Park in well-lit areas.\n" +
                "\t- Don’t leave valuables visible in the car.\n" +
                "\t- Be cautious of your surroundings when entering or exiting your vehicle.\n" +
                "\n4. Online Safety:\n\n" +
                "\t- Use strong, unique passwords.\n" +
                "\t- Be cautious with personal information on social media.\n" +
                "\t- Install reputable antivirus software.\n" +
                "\t- Be skeptical of unsolicited emails or messages.\n" +
                "\n5. Travel Safety:\n\n" +
                "\t- Research the safety of your destination before traveling.\n" +
                "\t- Keep valuables secure and be discreet.\n" +
                "\t- Inform someone of your travel plans.\n" +
                "\t- Be cautious in unfamiliar or high-risk areas.\n" +
                "\n6. Emergency Contacts:\n\n" +
                "\t- Have a list of emergency contacts easily accessible.\n" +
                "\t- Program emergency numbers into your phone.\n" +
                "\t- Establish a check-in system with friends or family.\n");
        return itemList;
    }
}
