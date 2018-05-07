package hotelapp.validators;

import hotelapp.models.Board;
import hotelapp.models.Boss;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class BossValidator implements Validator {
    @Override
    public boolean supports(Class clazz) {
        return Boss.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        Boss boss = (Boss) obj;

        if(boss.getSubscription() == 0) {
            if(boss.getWorkerAccounts() >= 3) {
                errors.rejectValue("workerAccounts", "too.many.worker.accounts.generated");
            }

            if(boss.getBoards().size() >= 5) {
                errors.rejectValue("boardsCount", "too.many.boards");
            }

            for(Board board : boss.getBoards()) {
                if(board.getTasks().size() >= 10) {
                    errors.rejectValue("workerAccounts", "too.many.tasks.in.board");
                }
            }
        } else if(boss.getSubscription() == 10) {
            if(boss.getWorkerAccounts() >= 5) {
                errors.rejectValue("workerAccounts", "too.many.worker.accounts.generated");
            }

            if(boss.getBoards().size() >= 8) {
                errors.rejectValue("workerAccounts", "too.many.boards");
            }

            for(Board board : boss.getBoards()) {
                if(board.getTasks().size() >= 15) {
                    errors.rejectValue("workerAccounts", "too.many.tasks.in.board");
                }
            }
        } else if(boss.getSubscription() == 20) {
            if(boss.getWorkerAccounts() >= 8) {
                errors.rejectValue("workerAccounts", "too.many.worker.accounts.generated");
            }

            if(boss.getBoards().size() >= 10) {
                errors.rejectValue("workerAccounts", "too.many.boards");
            }

            for(Board board : boss.getBoards()) {
                if(board.getTasks().size() >= 20) {
                    errors.rejectValue("workerAccounts", "too.many.tasks.in.board");
                }
            }
        }
    }
}
