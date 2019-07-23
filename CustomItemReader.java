package com.example.enpinterface.reader;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import com.example.enpinterface.model.Employee;


@Component
public class CustomItemReader implements ItemReader<Employee>{
 
	private int nextStudentIndex;
    private List<Employee> studentData;
 
    CustomItemReader() {
        initialize();
    }
 
    private void initialize() {
    	Employee tony = new Employee();
    	tony.setId("1");
        
    	Employee nick = new Employee();
    	nick.setId("2");
 
        studentData = Collections.unmodifiableList(Arrays.asList(tony, nick));
        nextStudentIndex = 0;
    }
 
    @Override
    public Employee read() throws Exception {
    	Employee nextStudent = null;
 
        if (nextStudentIndex < studentData.size()) {
            nextStudent = studentData.get(nextStudentIndex);
            nextStudentIndex++;
        }
 
        return nextStudent;
    }
 
 
}