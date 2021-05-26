package co.com.adventure.repository;

import co.com.adventure.model.Options;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionsRepository extends CrudRepository<Options, Integer> {
}
