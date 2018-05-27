package hotelapp.services;

import hotelapp.models.Boss;
import hotelapp.models.Type;

import java.util.List;

public interface TypeService {
    List<Type> getAllTypes();

    boolean typeExists(Integer id);

    Type findType(Integer id);

    Type findTypeByName(Boss boss, String typeName);

    void deleteType(Type type);

    void saveType(Type type);
}
