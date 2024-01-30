package recipes.table.util;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import recipes.table.api.model.User;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserUtil {

    private final ModelMapper modelMapper;

    public User dataToApi(recipes.table.data.model.User user) {
        return modelMapper.map(user, User.class);
    }
    public recipes.table.data.model.User apiToData(User user) {
        return modelMapper.map(user, recipes.table.data.model.User.class);
    }

    public List<User> convertListToApi(List<recipes.table.data.model.User> userList) {
        return userList.stream().map(this::dataToApi).collect(Collectors.toList());
    }
}
