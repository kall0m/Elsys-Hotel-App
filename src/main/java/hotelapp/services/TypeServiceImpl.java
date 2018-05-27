package hotelapp.services;

import hotelapp.models.Boss;
import hotelapp.models.Type;
import hotelapp.repositories.TypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TypeServiceImpl implements TypeService {
    private TypeRepository typeRepository;

    @Autowired
    public TypeServiceImpl(TypeRepository typeRepository) {
        this.typeRepository = typeRepository;
    }

    public List<Type> getAllTypes() {
        return typeRepository.findAll();
    }

    public boolean typeExists(Integer id) {
        return typeRepository.exists(id);
    }

    public Type findType(Integer id) {
        return typeRepository.findOne(id);
    }

    public Type findTypeByName(Boss boss, String typeName) {
        for(Type type : boss.getTypes()) {
            if(type.getName().equals(typeName)) {
                return type;
            }
        }

        return null;
    }

    public void deleteType(Type type) {
        typeRepository.delete(type);
    }

    public void saveType(Type type) {
        typeRepository.saveAndFlush(type);
    }
}
