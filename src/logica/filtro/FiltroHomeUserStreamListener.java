package logica.filtro;


import logica.Tweet;
import logica.excepciones.ExcepcionTweetFilter;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

/**
 * Escucha el stream de tweets que llegan a la cronología Home del usuario autenticado.
 * https://dev.twitter.com/docs/streaming-apis/messages#User_stream_messages
 * @author Eddie Contreras
 */
public class FiltroHomeUserStreamListener implements UserStreamListener{
	private Filtro filtro;
	
	public FiltroHomeUserStreamListener(Filtro filtro) {
		this.filtro = filtro;
	}
	
	@Override
	public void onDeletionNotice(StatusDeletionNotice arg0) {}

	@Override
	public void onScrubGeo(long arg0, long arg1) {}

	@Override
	public void onStallWarning(StallWarning arg0) {}

	/**
	 * Clasifica un tweet
	 */
	@Override
	public void onStatus(Status estado) {
		Tweet tweet = new Tweet(estado);
		filtro.clasificar(tweet);
	}

	@Override
	public void onTrackLimitationNotice(int arg0) {}

	@Override
	public void onException(Exception arg0) {
		try {
			filtro.apagar();
		} catch (ExcepcionTweetFilter e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onBlock(User arg0, User arg1) {}

	@Override
	public void onDeletionNotice(long arg0, long arg1) {}

	@Override
	public void onDirectMessage(DirectMessage arg0) {}

	@Override
	public void onFavorite(User arg0, User arg1, Status arg2) {}

	@Override
	public void onFollow(User arg0, User arg1) {}

	@Override
	public void onFriendList(long[] arg0) {}

	@Override
	public void onUnblock(User arg0, User arg1) {}

	@Override
	public void onUnfavorite(User arg0, User arg1, Status arg2) {}

	@Override
	public void onUserListCreation(User arg0, UserList arg1) {}

	@Override
	public void onUserListDeletion(User arg0, UserList arg1) {}

	@Override
	public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {}

	@Override
	public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {}

	@Override
	public void onUserListSubscription(User arg0, User arg1, UserList arg2) {}

	@Override
	public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {}

	@Override
	public void onUserListUpdate(User arg0, UserList arg1) {}

	@Override
	public void onUserProfileUpdate(User arg0) {}

}
