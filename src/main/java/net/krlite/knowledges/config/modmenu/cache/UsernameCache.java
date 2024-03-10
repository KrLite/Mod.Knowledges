package net.krlite.knowledges.config.modmenu.cache;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.ProfileResult;
import net.krlite.knowledges.KnowledgesClient;
import net.minecraft.client.MinecraftClient;

import java.util.*;

public class UsernameCache extends Cache<UUID, String> {
    private final Set<UUID> downloading = Collections.synchronizedSet(new HashSet<>());

    public UsernameCache() {
        super("username");
    }

    @Override
    protected boolean validate(String value) {
        return !value.isEmpty() && !value.contains("§");
    }

    @Override
    public Optional<String> get(UUID key) {
        if (!containsKey(key)) {
            download(key);
        }

        return super.get(key);
    }

    private void download(UUID uuid) {
        if (downloading.contains(uuid)) return;
        new DownloadThread(this, uuid).start();
    }

    private static class DownloadThread extends Thread {
        private final UsernameCache callback;
        private final UUID uuid;

        public DownloadThread(UsernameCache callback, UUID uuid) {
            this.callback = callback;
            this.uuid = uuid;

            callback.downloading.add(uuid);
        }

        @Override
        public void run() {
            try {
                ProfileResult profileResult = MinecraftClient.getInstance().getSessionService().fetchProfile(uuid, true);
                if (profileResult == null) {
                    return;
                }

                GameProfile profile = profileResult.profile();
                if (profile.getName() == null || profile.getName().equals("???")) {
                    return;
                }

                callback.put(profile.getId(), profile.getName());
                callback.downloading.remove(uuid);
            } catch (Exception e) {
                KnowledgesClient.LOGGER.error("Error downloading player profile!", e);
            }
        }
    }
}