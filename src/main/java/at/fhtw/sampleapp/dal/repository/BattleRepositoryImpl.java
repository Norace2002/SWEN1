package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BattleRepositoryImpl implements BattleRepository{

    private final UnitOfWork unitOfWork;

    public BattleRepositoryImpl(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }


}
