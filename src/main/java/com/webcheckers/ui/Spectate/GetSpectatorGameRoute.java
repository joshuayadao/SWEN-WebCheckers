package com.webcheckers.ui.Spectate;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import com.webcheckers.ui.BoardView;
import com.webcheckers.ui.Home.GetHomeRoute;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GetSpectatorGameRoute implements Route {

    private static final Logger LOG = Logger.getLogger(GetSpectatorGameRoute.class.getName());

    private static final String VIEW_NAME = "game.ftl";

    private final TemplateEngine templateEngine;

    private final PlayerLobby playerLobby;

    private final GameCenter gameCenter;

    private final Gson gson;

    public GetSpectatorGameRoute(final TemplateEngine templateEngine, final GameCenter gameCenter
    , final PlayerLobby playerLobby, final Gson gson) {
        this.templateEngine = templateEngine;
        this.gameCenter = gameCenter;
        this.playerLobby = playerLobby;
        this.gson = gson;
        LOG.config("GetSpectatorGameRoute is initialized.");
    }

    @Override
    public Object handle(Request request, Response response) {
        LOG.finer("GetSpectatorGameRoute is invoked.");
        final Map<String, Object> vm = new HashMap<>();
        Session currentSession = request.session();
        Player currentUser = currentSession.attribute(GetHomeRoute.CURRENT_USER_ATTR);

        String gameID = request.queryParams("gameID");

        Game gameToSpec = gameCenter.getGame(gameID);

        gameToSpec.updateSpectator(currentUser);


        vm.put(GetHomeRoute.CURRENT_USER_ATTR, currentUser);
        vm.put("viewMode", GameCenter.ViewMode.SPECTATOR);
        vm.put("redPlayer", gameToSpec.getRedPlayer());
        vm.put("whitePlayer", gameToSpec.getWhitePlayer());
        vm.put("activeColor", gameToSpec.getActivePlayer().getPlayerColor());
        boolean isRed;
        if (gameToSpec.getActivePlayer().equals(gameToSpec.getRedPlayer())) isRed = true;
        else isRed = false;
        vm.put("board", new BoardView(gameToSpec.getSpectatorBoard(currentUser),
                isRed));

        vm.put("title", "Enjoy watching your game!");
        return templateEngine.render(new ModelAndView(vm, VIEW_NAME));
    }
}
