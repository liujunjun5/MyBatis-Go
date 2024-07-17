import java.util.List;

public interface IUserDao {
    User queryUserInfoById(Long id);

    List<User> queryUserList(User user);
}
