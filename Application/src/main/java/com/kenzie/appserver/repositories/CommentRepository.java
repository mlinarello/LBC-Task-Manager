package com.kenzie.appserver.repositories;

import com.kenzie.appserver.repositories.model.CommentRecord;
import com.kenzie.appserver.repositories.model.TaskRecord;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface CommentRepository extends CrudRepository<CommentRecord, String> {

}
