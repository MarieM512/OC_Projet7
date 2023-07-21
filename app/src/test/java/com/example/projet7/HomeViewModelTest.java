package com.example.projet7;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.projet7.data.OkhttpService;
import com.example.projet7.data.RestaurantRepository;
import com.example.projet7.firebase.FirebaseService;
import com.example.projet7.ui.viewmodel.HomeViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class HomeViewModelTest {

    OkhttpService mOkhttpService = Mockito.mock(OkhttpService.class);
    FirebaseFirestore mFirebaseFirestore = Mockito.mock(FirebaseFirestore.class);
    FirebaseUser mFirebaseUser = Mockito.mock(FirebaseUser.class);

    RestaurantRepository mRestaurantRepository;
    FirebaseService mFirebaseService;
    HomeViewModel viewModel;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mRestaurantRepository = new RestaurantRepository(mOkhttpService);

        mFirebaseService = new FirebaseService(mFirebaseFirestore);
        viewModel = new HomeViewModel(mRestaurantRepository, mFirebaseService, mFirebaseUser);

        String api = "{\"results\":[{\"fsq_id\":\"44a27278f964a520da341fe3\",\"categories\":[{\"id\":13276,\"name\":\"Sushi Restaurant\",\"icon\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/food/sushi_\",\"suffix\":\".png\"}}],\"chains\":[],\"distance\":327,\"geocodes\":{\"main\":{\"latitude\":37.319936,\"longitude\":-122.03291},\"roof\":{\"latitude\":37.319936,\"longitude\":-122.03291}},\"link\":\"/v3/places/44a27278f964a520da341fe3\",\"location\":{\"address\":\"10211 S De Anza Blvd\",\"census_block\":\"060855077013013\",\"country\":\"US\",\"cross_street\":\"\",\"dma\":\"San Francisco-Oakland-San Jose\",\"formatted_address\":\"10211 S De Anza Blvd, Cupertino, CA 95014\",\"locality\":\"Cupertino\",\"postcode\":\"95014\",\"region\":\"CA\"},\"name\":\"Sushi Kuni\",\"related_places\":{},\"timezone\":\"America/Los_Angeles\"}]}";
        Mockito.when(mOkhttpService.getResponseApi()).thenReturn(api);
        Mockito.when(mOkhttpService.getUrlImgRV("Sushi Kuni")).thenReturn("ImgRV");
        Mockito.when(mOkhttpService.getUrlImgDetail("Sushi Kuni")).thenReturn("ImgDetail");

        Mockito.when(mFirebaseUser.getDisplayName()).thenReturn("Marie");
        Mockito.when(mFirebaseUser.getEmail()).thenReturn("marie@live.fr");
        Mockito.when(mFirebaseUser.getPhotoUrl()).thenReturn(Uri.parse("https://play-lh.googleusercontent.com/YBChvJfwfwtGHAPiPYLn-c5jCMXS0p2CyT1TWrsFtjyrPn9foIMjLf62UuRUccwAwTI"));

    }

    /* Restaurant */

    @Test
    public void getRestaurantApi() {
        String api = mOkhttpService.getResponseApi();
        Assert.assertEquals(api, "{\"results\":[{\"fsq_id\":\"44a27278f964a520da341fe3\",\"categories\":[{\"id\":13276,\"name\":\"Sushi Restaurant\",\"icon\":{\"prefix\":\"https://ss3.4sqi.net/img/categories_v2/food/sushi_\",\"suffix\":\".png\"}}],\"chains\":[],\"distance\":327,\"geocodes\":{\"main\":{\"latitude\":37.319936,\"longitude\":-122.03291},\"roof\":{\"latitude\":37.319936,\"longitude\":-122.03291}},\"link\":\"/v3/places/44a27278f964a520da341fe3\",\"location\":{\"address\":\"10211 S De Anza Blvd\",\"census_block\":\"060855077013013\",\"country\":\"US\",\"cross_street\":\"\",\"dma\":\"San Francisco-Oakland-San Jose\",\"formatted_address\":\"10211 S De Anza Blvd, Cupertino, CA 95014\",\"locality\":\"Cupertino\",\"postcode\":\"95014\",\"region\":\"CA\"},\"name\":\"Sushi Kuni\",\"related_places\":{},\"timezone\":\"America/Los_Angeles\"}]}");
    }

    @Test
    public void getRestaurantName() {
        String name = viewModel.getName(0);
        Assert.assertEquals(name, "Sushi Kuni");
    }

    @Test
    public void getRestaurantType() {
        String type = viewModel.getType(0);
        Assert.assertEquals(type, "Sushi Restaurant");
    }

    @Test
    public void getRestaurantId() {
        String id = viewModel.getId(0);
        Assert.assertEquals(id, "44a27278f964a520da341fe3");
    }

    @Test
    public void getRestaurantDistance() {
        String distance = viewModel.getDistance(0);
        Assert.assertEquals(distance, "327m");
    }

    @Test
    public void getRestaurantAddress() {
        String address = viewModel.getAddress(0);
        Assert.assertEquals(address, "10211 S De Anza Blvd");
    }

    @Test
    public void getUrlImgRV() {
        String url = viewModel.getImgRV(0);
        Assert.assertEquals(url, "ImgRV");
    }

    @Test
    public void getUrlImgDetail() {
        String url = viewModel.getImgDetail(0);
        Assert.assertEquals(url, "ImgDetail");
    }

    @Test
    public void getUrlImgRVByName() {
        String url = viewModel.getImgRVByName("Sushi Kuni");
        Assert.assertEquals(url, "ImgRV");
    }

    @Test
    public void getLunchById() {
        HashMap<String, String> result = viewModel.getLunchById("44a27278f964a520da341fe3");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("name", "Sushi Kuni");
        hashMap.put("type", "Sushi Restaurant");
        hashMap.put("address", "10211 S De Anza Blvd");
        hashMap.put("image", "ImgDetail");
        Assert.assertEquals(result, hashMap);
    }

    @Test
    public void getLunchByName() {
        HashMap<String, String> result = viewModel.getLunchByName("Sushi Kuni");
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", "44a27278f964a520da341fe3");
        hashMap.put("type", "Sushi Restaurant");
        hashMap.put("address", "10211 S De Anza Blvd");
        hashMap.put("image", "ImgDetail");
        Assert.assertEquals(result, hashMap);
    }

    @Test
    public void goToRestaurantById() {
        Bundle result = viewModel.goToRestaurantById(false, "44a27278f964a520da341fe3");
        Bundle bundle = new Bundle();
        bundle.putString("id", "44a27278f964a520da341fe3");
        bundle.putString("name", "Sushi Kuni");
        bundle.putString("type", "Sushi Restaurant");
        bundle.putString("address", "10211 S De Anza Blvd");
        bundle.putString("image", "ImgDetail");
        Assert.assertEquals(result.get("id"), bundle.get("id"));
        Assert.assertEquals(result.get("name"), bundle.get("name"));
        Assert.assertEquals(result.get("type"), bundle.get("type"));
        Assert.assertEquals(result.get("address"), bundle.get("address"));
        Assert.assertEquals(result.get("image"), bundle.get("image"));
    }

    @Test
    public void goToRestaurantByPosition() {
        Bundle result = viewModel.goToRestaurantById(true, 0);
        Bundle bundle = new Bundle();
        bundle.putString("id", "44a27278f964a520da341fe3");
        bundle.putString("name", "Sushi Kuni");
        bundle.putString("type", "Sushi Restaurant");
        bundle.putString("address", "10211 S De Anza Blvd");
        bundle.putString("image", "ImgDetail");
        Assert.assertEquals(result.get("id"), bundle.get("id"));
        Assert.assertEquals(result.get("name"), bundle.get("name"));
        Assert.assertEquals(result.get("type"), bundle.get("type"));
        Assert.assertEquals(result.get("address"), bundle.get("address"));
        Assert.assertEquals(result.get("image"), bundle.get("image"));
    }

    /* Current User */

    @Test
    public void getNameCurrentUser() {
        String name = viewModel.getNameUser();
        Assert.assertEquals(name, "Marie");
    }

    @Test
    public void getEmailCurrentUser() {
        String name = viewModel.getEmailUser();
        Assert.assertEquals(name, "marie@live.fr");
    }

    @Test
    public void getImageCurrentUser() {
        Uri image = viewModel.getImgUser();
        Assert.assertEquals(image, Uri.parse("https://play-lh.googleusercontent.com/YBChvJfwfwtGHAPiPYLn-c5jCMXS0p2CyT1TWrsFtjyrPn9foIMjLf62UuRUccwAwTI"));
    }
}
