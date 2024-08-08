"use client";
import CustomSkeleton from '@/components/custom-skeleton';
import { SearchIcon } from '@/components/icons/searchicon';
import {
    Autocomplete,
    AutocompleteItem,
    Button,
    Input,
    RadioGroup,
    Switch,
    Tab,
    Table,
    TableBody,
    TableCell,
    TableColumn,
    TableHeader,
    TableRow,
    Modal,
    ModalContent,
    ModalHeader,
    ModalBody,
    ModalFooter,
    useDisclosure,
    Chip,
    Textarea,
} from '@nextui-org/react';
import dynamic from 'next/dynamic';
import React, { useMemo } from 'react';
import { useGetAutoComplete, useGetSearch, useGetDirections } from '@/services/mapService';
import _ from 'lodash';
import LocationIcon from '@/components/icons/location-icon';
import { useGetStudentRides } from '@/services/client/parentStudentService';
import { useAddRequestRegistration, useGetRequestRegistration } from '@/services/client/clientRequestRegistrationService';
import { on } from 'events';
import { convertStringInstantToDate } from '@/util/dateConverter';



const ClientRegistration: React.FC = () => {

    // get auto complete
    const [autoCompleteQuery, setAutoCompleteQuery] = React.useState('');
    let autoCompleteParams: IAutoCompleteParams = {
        text: autoCompleteQuery,
        boundary: {
            country: 'VNM'
        }
    }
    const debounceSetAutoCompleteQuery = _.debounce((value: string) => setAutoCompleteQuery(value), 500);
    const { data: autoCompleteData, isLoading: autoCompleteLoading, error: autoCompleteError } = useGetAutoComplete(autoCompleteParams);
    const [selectedAutoCompleteData, setSelectedAutoCompleteData] = React.useState<IFeature | null>(null);

    // get directions
    const useGetDirectionsParams: IDirectionsParams = {
        coordinates: [[105.804817, 21.028511], [105.803577, 21.03422], [105.80325113694303, 21.03586062789824]]
    }
    const { data: directionsGetResponse, isLoading: directionsLoading, error: directionsError } = useGetDirections(useGetDirectionsParams);

    // get student rides
    const getStudentRidesParams: IGetStudentRidesParams = {
    }
    const { data: studentRidesData, isLoading: studentRidesLoading, error: studentRidesError } = useGetStudentRides(getStudentRidesParams);

    const Map = useMemo(() => dynamic(
        () => import('@/components/map/MapClient'),
        {
            loading: () => <CustomSkeleton />,
            ssr: false
        }
    ), [])

    // get request registration
    const getRequestRegistrationParams: IGetRequestRegistrationParams = {

    }
    const { data: requestRegistrationData, isLoading: requestRegistrationLoading, error: requestRegistrationError } = useGetRequestRegistration(getRequestRegistrationParams);

    // add request registration
    const [studentIds, setStudentIds] = React.useState<number[]>([]);
    const { isOpen: isOpenAddRegistrationConfirm, onOpen: onOpenAddRegistrationConfirm, onOpenChange: onOpenChangeAddRegistrationConfirm } = useDisclosure();
    let addRequestRegistrationParams: IAddRequestRegistrationRequest = {
        studentIds: studentIds,
        address: selectedAutoCompleteData?.properties?.name ?
            `${selectedAutoCompleteData?.properties?.name}, ${selectedAutoCompleteData?.properties?.county}, ${selectedAutoCompleteData?.properties?.region}, ${selectedAutoCompleteData?.properties?.country}`
            : '',
        latitude: selectedAutoCompleteData?.geometry?.coordinates[1] ?? null,
        longitude: selectedAutoCompleteData?.geometry?.coordinates[0] ?? null
    }
    const addRequestRegistrationMutation = useAddRequestRegistration(() => {
        onOpenChangeAddRegistrationConfirm();
    });
    const handleAddRequestRegistration = () => {
        addRequestRegistrationMutation.mutate(addRequestRegistrationParams);
    }

    // enable click point to map
    const [enableClickMap, setEnableClickMap] = React.useState<boolean>(false);

    console.log('autoCompleteData', autoCompleteData);

    return (
        <div className='flex flex-col'>
            <div className='flex justify-between w-auto'>
                <div className='w-2/5 mr-4'>
                    <Autocomplete
                        placeholder="Nhập địa điểm"
                        allowsCustomValue
                        startContent={<SearchIcon />}
                        items={autoCompleteData?.features.map((item) => item.properties) || []}
                        className="m-2 w-full"
                        selectorIcon={false}
                        onInputChange={(value) => {
                            debounceSetAutoCompleteQuery(value);
                        }}
                        onSelectionChange={
                            (selectedId) => {
                                const selectedItem = autoCompleteData?.features.find((item) => item.properties.id === selectedId);
                                if (selectedItem) {
                                    setSelectedAutoCompleteData(selectedItem);
                                }
                            }
                        }
                        isClearable={false}
                    >
                        {(item) =>
                            <AutocompleteItem
                                key={item.id}
                                textValue={`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                            >
                                <div className="flex gap-2 items-center">
                                    <div className='flex-shrink-0'>
                                        <LocationIcon />
                                    </div>

                                    <div className='flex flex-col'>
                                        {`${item.name ?? ''}, ${item.county ?? ''}, ${item.region ?? ''}, ${item.country ?? ''}`}
                                    </div>
                                </div>

                            </AutocompleteItem>
                        }
                    </Autocomplete>

                    <div className='flex justify-between'>
                        <div></div>

                        <Button
                            color={enableClickMap ? 'primary' : 'default'}
                            onClick={() => setEnableClickMap(!enableClickMap)}
                        >
                            Chấm điểm
                        </Button>
                    </div>

                    {/* table student rides */}
                    <div className='m-2'>
                        <Table
                            aria-label="List student"
                            selectionMode='multiple'
                            onSelectionChange={(selectedKeys) => {
                                if (selectedKeys === 'all') {
                                    const keysArray = studentRidesData?.result?.map(item => item.student.id) ?? [];
                                    setStudentIds(keysArray);
                                } else {
                                    const keysArray = Array.from(selectedKeys).map(Number);
                                    setStudentIds(keysArray);
                                }
                            }}
                        >
                            <TableHeader columns={[
                                { key: 'student', label: 'Học sinh' },
                                { key: 'address', label: 'Điểm đón' },
                                { key: 'numberPlateAssign', label: 'Xe chỉ định' }
                            ]}>
                                {(column) => <TableColumn key={column.key}>{column.label}</TableColumn>}
                            </TableHeader>

                            <TableBody items={studentRidesData?.result ?? []} emptyContent='No row to display'>
                                {(item) => (
                                    <TableRow key={item.student.id}>
                                        <TableCell>{item?.student?.name}</TableCell>
                                        <TableCell>{item?.pickupPoint?.address}</TableCell>
                                        <TableCell>{item?.student?.numberPlateAssign}</TableCell>
                                    </TableRow>
                                )}
                            </TableBody>
                        </Table>
                    </div>

                    <div className='flex justify-end'>
                        <Button
                            color='primary'
                            onClick={onOpenAddRegistrationConfirm}
                        >
                            Đăng ký
                        </Button>
                    </div>

                    {/* a text note for client */}
                    <div className='m-2'>
                        <Textarea
                            isReadOnly
                            defaultValue='Ghi chú: Xe chỉ định chỉ là xe học sinh được ưu tiên hơn so với học sinh khác nếu xe đó đến. Học sinh vẫn có thể được phụ xe ở xe khác điểm danh và gọi đón.'
                            color='danger'
                        >
                        </Textarea>
                    </div>

                </div>

                {/* map */}
                <div
                    className="w-3/5 z-50"
                >
                    <Map
                        features={selectedAutoCompleteData ? [selectedAutoCompleteData] : []}
                        directionsGetResponse={directionsGetResponse}
                        enableClickMap={enableClickMap}
                        ourPickupPoints={studentRidesData?.result?.map(item => item.pickupPoint).filter(pickupPoint => pickupPoint !== null) ?? []} />
                </div >
            </div>


            {/* table registration */}
            <div className='m-4'>
                <Table
                    aria-label="List registration"
                    // selectionMode='multiple'
                    onSelectionChange={(selectedKeys) => {

                    }}
                >
                    <TableHeader columns={[
                        { key: 'student', label: 'Học sinh' },
                        { key: 'address', label: 'Địa điểm đăng ký' },
                        { key: 'status', label: 'Trạng thái' },
                        { key: 'ngày đăng ký', label: 'Ngày đăng ký' },
                        { key: 'note', label: 'Ghi chú' }
                    ]}>
                        {(column) => <TableColumn key={column.key}>{column.label}</TableColumn>}
                    </TableHeader>

                    <TableBody items={requestRegistrationData?.result ?? []} emptyContent='No row to display'>
                        {(item) => (
                            <TableRow key={item.requestRegistration.id}>
                                <TableCell>{item.student.name}</TableCell>
                                <TableCell>{item.requestRegistration.address}</TableCell>
                                <TableCell>
                                    <Chip
                                        variant='flat'
                                        color={
                                            item.requestRegistration.status === 'PENDING' ? 'warning' :
                                                item.requestRegistration.status === 'ACCEPTED' ? 'success' :
                                                    item.requestRegistration.status === 'REJECTED' ? 'danger' : 'default'

                                        }
                                    >
                                        {item.requestRegistration.status}
                                    </Chip>
                                </TableCell>
                                <TableCell>{convertStringInstantToDate(item.requestRegistration.createdAt)}</TableCell>
                                <TableCell>{item.requestRegistration.note}</TableCell>
                            </TableRow>
                        )}
                    </TableBody>
                </Table>
            </div>


            {/* modal confirm adding request registration */}
            <div className='relative z-10'>
                <Modal isOpen={isOpenAddRegistrationConfirm} onOpenChange={onOpenChangeAddRegistrationConfirm}
                >
                    <ModalContent>
                        {(onOpenAddRegistrationConfirm) => (
                            <>
                                <ModalHeader className="flex flex-col gap-1">Xác nhận thêm chuyến</ModalHeader>
                                <ModalBody>
                                    <p>
                                        Bạn có muốn đăng ký địa điểm này không?
                                    </p>
                                </ModalBody>
                                <ModalFooter>
                                    <Button color="danger" variant="light"
                                        onPress={onOpenChangeAddRegistrationConfirm}
                                    >
                                        Huỷ
                                    </Button>
                                    <Button color="primary" onPress={() => {
                                        handleAddRequestRegistration();
                                    }}>
                                        Xác nhận
                                    </Button>
                                </ModalFooter>
                            </>
                        )}
                    </ModalContent>
                </Modal>
            </div>
        </div>
    );
};

export default ClientRegistration;