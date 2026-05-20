package de.vw.paso.service.partlist;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

import de.vw.paso.exception.CannotResolveOnServerSideException;
import de.vw.paso.exception.EmptyListException;
import de.vw.paso.exception.NullElementException;
import de.vw.paso.partlist.domain.IPartListChildDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementDTO;
import de.vw.paso.service.partlist.efsedit.EfsElementMaraDTO;
import org.jspecify.annotations.NonNull;

public class EfsEditValidations implements ICopyEfsElementConsumer, IDeleteEfsElementConsumer, IMoveEfsElementConsumer,
        ISaveEfsElementConsumer {

    private static final Pattern MARA_PART_NUMBER = Pattern.compile("[\\w ]{7,16}");

    private final Function<Long, EfsElementDTO> findElement;
    private final Function<EfsElementDTO, List<Long>> findChildren;

    public EfsEditValidations(Function<Long, EfsElementDTO> findElement,
            Function<EfsElementDTO, List<Long>> findChildren) {
        this.findElement = findElement;
        this.findChildren = findChildren;
    }

    @Override
    public void moveEfsElements(@NonNull IPartListChildDTO toParent, @NonNull List<EfsElementDTO> efsElements)
            throws EmptyListException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            NullElementException, AppendToDeletedElementException, MovingHierachyConflictException {
        prepareCopyEfsElements(efsElements);
        preparePasteAfterCut(toParent, efsElements);
    }

    public static boolean evaluateMaraIsAppropriate(String partNumber)
            throws NullElementException, PartNumberInappropriateException {
        nullElements(partNumber);

        if (!MARA_PART_NUMBER.matcher(partNumber).matches()
            //        Disabling the condition as some part numbers can skip the pattern.
            //      && !Pattern.matches("[\\w ]{3}\\.[\\w ]{3}\\.[\\w ]{3}\\.[\\w ]{0,2}", partNumber)
        ) {
            throw new PartNumberInappropriateException(partNumber);
        }

        return true;
    }

    public void preparePasteAfterCut(IPartListChildDTO toParent, Collection<EfsElementDTO> efsElements)
            throws NullElementException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            AppendToDeletedElementException, EmptyListException, MovingHierachyConflictException {
        nullElements(toParent);
        checkNewParentNotDeleted(toParent);
        removeElementsThatWontChange(toParent, efsElements);

        for (EfsElementDTO elementToMove : efsElements) {
            EfsElementDTO parent = toParent.asParent();
            if (parent == null) {
                continue;
            }

            checkIfParentIsInChildrenHierarchy(parent, elementToMove);
            for (Long id : findChildren.apply(elementToMove)) {
                ensureMaraIsNotUsedInParent(parent, parent.getId(), findElement.apply(id).getEfsElementMara());
            }
        }

        emptyList(efsElements);
    }

    @Override
    public void deleteEfsElement(List<EfsElementDTO> efsElements) throws EmptyListException, NullElementException {
        baseCheck(efsElements);
    }

    //todo: same as with deleteEfsElement -> simplify to one method 
    public void prepareCopyEfsElements(Collection<EfsElementDTO> efsElements)
            throws NullElementException, EmptyListException {
        baseCheck(efsElements);
    }

    public void preparePasteAfterCopy(IPartListChildDTO newParent, List<EfsElementDTO> elementsToCopy)
            throws NullElementException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            AppendToDeletedElementException {
        nullElements(newParent);
        checkNewParentNotDeleted(newParent);

        for (EfsElementDTO elementToCopy : elementsToCopy) {
            EfsElementDTO parent = newParent.asParent();
            if (parent == null) {
                continue;
            }

            for (Long id : findChildren.apply(elementToCopy)) {
                ensureMaraIsNotUsedInParent(parent, parent.getId(), findElement.apply(id).getEfsElementMara());
            }
        }
    }

    @Override
    public void copyEfsElements(IPartListChildDTO newParent, List<EfsElementDTO> efsElements)
            throws EmptyListException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            NullElementException, AppendToDeletedElementException {
        prepareCopyEfsElements(efsElements);
        preparePasteAfterCopy(newParent, efsElements);
    }

    @Override
    public void handle(EmptyListException exception) {
        fail();
    }

    @Override
    public void handle(NullElementException exception) {
        fail();
    }

    @Override
    public void handle(AppendToDeletedElementException exception) {
        fail();
    }

    @Override
    public void handle(MovingHierachyConflictException e) {
        fail();
    }

    @Override
    public void handle(PartNumberInappropriateException e) {
        fail();
    }

    @Override
    public void handle(SameMaraInHierachyException exception) {
        fail();
    }

    @Override
    public void saveEfsElement(EfsElementDTO efsElement)
            throws NullElementException, CannotResolveOnServerSideException, SameMaraInHierachyException,
            PartNumberInappropriateException, CreateDeletedEfsElementException, EditingDeletedEfsElementException {
        nullElements(efsElement);
        nullElements(efsElement.getEfsElementMara());
        evaluateMaraIsAppropriate(efsElement.getEfsElementMara().getPartNumber());
        ensureMaraIsNotUsedInParent(efsElement.getParent(), efsElement.getParentId(), efsElement.getEfsElementMara());

        if (!efsElement.isDeleted()) {
            return;
        }

        if (efsElement.getId() == null) {
            throw new CreateDeletedEfsElementException(efsElement);
        }

        throw new EditingDeletedEfsElementException(efsElement);
    }

    private void checkIfParentIsInChildrenHierarchy(EfsElementDTO parent, EfsElementDTO child)
            throws MovingHierachyConflictException {
        if (parent == null) {
            return;
        }

        if (parent.getId().equals(child.getId())) {
            throw new MovingHierachyConflictException(parent);
        }

        checkIfParentIsInChildrenHierarchy(parent.getParent(), child);
    }

    private void fail() {
        assert false : "Should not be called!";
    }

    private static void nullElements(Object element) throws NullElementException {
        if (element == null) {
            throw new NullElementException();
        }
    }

    private void emptyList(Collection<EfsElementDTO> elements) throws EmptyListException, NullElementException {
        if (elements == null) {
            throw new NullElementException();
        }

        long deletedElements = elements.stream().filter(element -> !element.getDeleted().equals(0)).count();
        if (deletedElements == elements.size()) {
            throw new EmptyListException();
        }
    }

    private void checkNewParentNotDeleted(IPartListChildDTO parent) throws AppendToDeletedElementException {
        if (parent.asParent() != null && parent.asParent().isDeleted()) {
            throw new AppendToDeletedElementException(parent.asParent());
        }
    }

    private void baseCheck(Collection<EfsElementDTO> efsElements) throws EmptyListException, NullElementException {
        emptyList(efsElements);
    }

    private void removeElementsThatWontChange(IPartListChildDTO newParent, Collection<EfsElementDTO> efsElements) {
        for (EfsElementDTO element : efsElements) {
            Long sourceParentId = element.getParentId();
            EfsElementDTO targetParent = newParent.asParent();

            if ((sourceParentId == null && targetParent != null) || (sourceParentId != null && targetParent == null)) {
                continue;
            }

            Long sourcePartList = element.getVehiclePartListId();
            Long targetPartList = newParent.getVehiclePartListId();

            if (sourceParentId == null) {
                if (sourcePartList.equals(targetPartList)) {
                    efsElements.remove(element);
                }

                continue;
            }

            if (sourceParentId.equals(targetParent.getId()) && sourcePartList.equals(targetPartList)) {
                efsElements.remove(element);
            }
        }
    }

    private void ensureMaraIsNotUsedInParent(EfsElementDTO parent, Long parentId, EfsElementMaraDTO mara)
            throws SameMaraInHierachyException, CannotResolveOnServerSideException {
        if (parentId == null && parent == null) {
            return;
        }

        if (parent == null) {
            parent = findElement.apply(parentId);
        }

        // this should not happen, however, due to defensive programming it should stay here
        if (parent == null) {
            throw new CannotResolveOnServerSideException("Parent with id " + parentId + " cannot be resolved");
        }

        if (parent.getEfsElementMara().getPartNumber().equals(mara.getPartNumber())) {
            throw new SameMaraInHierachyException(mara.getPartNumber());
        }

        ensureMaraIsNotUsedInParent(parent.getParent(), parent.getParentId(), mara);
    }
}