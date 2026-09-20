/*
 * Copyright 2026 Morphe.
 * https://github.com/MorpheApp/morphe-patches
 *
 * See the included NOTICE file for GPLv3 Section 7 terms that apply to this code.
 */

package app.morphe.extension.youtube.videoplayer;

import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import app.morphe.extension.shared.Logger;
import app.morphe.extension.shared.ResourceType;
import app.morphe.extension.shared.ResourceUtils;
import app.morphe.extension.shared.Utils;
import app.morphe.extension.youtube.patches.LegacyPlayerControlsPatch;
import app.morphe.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class PlaylistAutoplayButton {
    private static WeakReference<ImageView> overlayButtonRef = new WeakReference<>(null);

    /**
     * Injection point.
     */
    public static void initializeButton(View controlsView) {
        try {
            // YouTube 21.36+ uses the current/bold controls. Do not inject this
            // custom button when legacy controls are explicitly restored on older targets.
            if (LegacyPlayerControlsPatch.RESTORE_OLD_PLAYER_BUTTONS) {
                return;
            }

            overlayButtonRef = new WeakReference<>(PlayerOverlayButton.addButton(
                    controlsView,
                    getIconName(),
                    view -> togglePlaylistAutoplay(),
                    null
            ));
        } catch (Exception ex) {
            Logger.printException(() -> "initializeButton failure", ex);
        }
    }

    private static void togglePlaylistAutoplay() {
        try {
            Settings.DISABLE_PLAYLIST_AUTOPLAY.save(
                    !Settings.DISABLE_PLAYLIST_AUTOPLAY.get()
            );
            updateButtonIcon();
        } catch (Exception ex) {
            Logger.printException(() -> "togglePlaylistAutoplay failure", ex);
        }
    }

    private static void updateButtonIcon() {
        Utils.verifyOnMainThread();

        ImageView button = overlayButtonRef.get();
        if (button != null) {
            button.setImageResource(ResourceUtils.getIdentifierOrThrow(
                    ResourceType.DRAWABLE,
                    getIconName()
            ));
        }
    }

    private static String getIconName() {
        // Icon describes playlist autoplay itself:
        // OFF = stop when the current playlist video ends.
        // ON  = advance to the next playlist video.
        return Settings.DISABLE_PLAYLIST_AUTOPLAY.get()
                ? "morphe_playlist_autoplay_off_bold"
                : "morphe_playlist_autoplay_on_bold";
    }
}
