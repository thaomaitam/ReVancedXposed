package app.revanced.integrations.youtube.settings;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import app.revanced.integrations.shared.Logger;
import app.revanced.integrations.shared.settings.*;
@SuppressWarnings("deprecation")
public class Settings extends BaseSettings {
    // Ads
    public static final BooleanSetting HIDE_FULLSCREEN_ADS = new BooleanSetting("revanced_hide_fullscreen_ads", TRUE);
    public static final BooleanSetting HIDE_BUTTONED_ADS = new BooleanSetting("revanced_hide_buttoned_ads", TRUE);
    public static final BooleanSetting HIDE_GENERAL_ADS = new BooleanSetting("revanced_hide_general_ads", TRUE);
    public static final BooleanSetting HIDE_GET_PREMIUM = new BooleanSetting("revanced_hide_get_premium", TRUE);
    public static final BooleanSetting HIDE_HIDE_LATEST_POSTS = new BooleanSetting("revanced_hide_latest_posts_ads", TRUE);
    public static final BooleanSetting HIDE_MERCHANDISE_BANNERS = new BooleanSetting("revanced_hide_merchandise_banners", TRUE);
    public static final BooleanSetting HIDE_PAID_PROMOTION_LABEL = new BooleanSetting("revanced_hide_paid_promotion_label", TRUE);
    public static final BooleanSetting HIDE_PRODUCTS_BANNER = new BooleanSetting("revanced_hide_products_banner", TRUE);
    public static final BooleanSetting HIDE_SHOPPING_LINKS = new BooleanSetting("revanced_hide_shopping_links", TRUE);
    public static final BooleanSetting HIDE_SELF_SPONSOR = new BooleanSetting("revanced_hide_self_sponsor_ads", TRUE);
    public static final BooleanSetting HIDE_VIDEO_ADS = new BooleanSetting("revanced_hide_video_ads", TRUE, true);
    public static final BooleanSetting HIDE_VISIT_STORE_BUTTON = new BooleanSetting("revanced_hide_visit_store_button", TRUE);
    public static final BooleanSetting HIDE_WEB_SEARCH_RESULTS = new BooleanSetting("revanced_hide_web_search_results", TRUE);
}